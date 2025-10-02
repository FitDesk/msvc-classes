package com.classes.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class HelloController {
    @GetMapping("saludo")
    public ResponseEntity<String> saludo() {
        return ResponseEntity.ok("Microservicio de clases activo");
    }
}
