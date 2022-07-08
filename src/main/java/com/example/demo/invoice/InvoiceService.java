package com.example.demo.invoice;

import com.convertapi.client.Config;
import com.convertapi.client.ConvertApi;
import com.convertapi.client.Param;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;

@Component
public class InvoiceService {
    public HashMap get() {
        HashMap result = svgToText();
        return result;
    }

    public HashMap svgToText() {
        try {
            URL url = new URL("https://hoadondientu.gdt.gov.vn:30000/captcha");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            if (con.getResponseCode() != 200) {
                return new HashMap();
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            HashMap result = new ObjectMapper().readValue(content.toString(), HashMap.class);
            con.disconnect();

            String key = result.get("key").toString();
            BufferedWriter writer = new BufferedWriter(new FileWriter("yolo/test.svg", false));
            writer.write(result.get("content").toString());
            writer.close();

            Config.setDefaultSecret("HacXWULCPyKGExwm");
            ConvertApi.convert("svg", "jpg",
                    new Param("File", Paths.get("yolo/test.svg"))
            ).get().saveFilesSync(Paths.get("yolo"));


            File imageFile = new File("yolo/test.jpg");
            BufferedImage bufferedImage = ImageIO.read(imageFile);
            ImageIO.write(cropImage(bufferedImage, 12, 5, 180, 34),"jpg", imageFile);

            ProcessBuilder builder = new ProcessBuilder(
                    "cmd.exe", "/c", "cd yolo && darknet_no_gpu.exe detector test data/obj.data yolov3tinyobj.cfg yolov3tinyobjLast.weights -dont_show test.jpg");
            builder.redirectErrorStream(true);
            Process p = builder.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;

            StringBuilder text = new StringBuilder();
            while (true) {
                line = r.readLine();
                if (line == null) {
                    break;
                }
                if (line.matches("([A-Za-z\\d]: (10|[5-9])\\d%)")) {
                    text.append(line.charAt(0));
                }
                System.out.println(line);
            }

            System.out.println("result: " + text);



            URL url1 = new URL("https://hoadondientu.gdt.gov.vn:30000/query/guest-invoices?khmshdon=1&hdon=01&nbmst=4101472870&khhdon=C22TTE&shdon=5&tgtttbso=1240000&cvalue=" + text + "&ckey=" + key);
            HttpURLConnection con1 = (HttpURLConnection) url1.openConnection();
            con1.setRequestMethod("GET");

            BufferedReader in1 = new BufferedReader(new InputStreamReader(con1.getInputStream()));
            String inputLine1;
            StringBuffer content1 = new StringBuffer();
            while ((inputLine1 = in1.readLine()) != null) {
                content1.append(inputLine1);
            }
            in.close();
            HashMap result1 = new ObjectMapper().readValue(content1.toString(), HashMap.class);
            con.disconnect();
            return result1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap();
    }

    public BufferedImage cropImage(BufferedImage bufferedImage, int x, int y, int width, int height){
        return bufferedImage.getSubimage(x, y, width, height);
    }
}
