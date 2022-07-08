package com.example.demo.invoice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@Component
@RestController(value = "/invoice")
public class InvoiceResource {
    @Autowired
    InvoiceService invoiceService;

    @PostMapping("/invoice/test")
    public HashMap get() {
        return invoiceService.get();
    }
}
