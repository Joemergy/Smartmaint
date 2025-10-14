package com.smartmaint.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping
    public ResponseEntity<?> ping() {
        System.out.println("📡 TestController: ping recibido");

        Map<String, Object> estado = new HashMap<>();
        estado.put("mensaje", "Smartmaint backend activo");
        estado.put("timestamp", LocalDateTime.now());
        estado.put("status", "OK");

        return ResponseEntity.ok(estado);
    }
}
