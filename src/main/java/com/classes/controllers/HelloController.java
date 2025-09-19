package com.classes.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/saludo")
public class HelloController {
    public String saludo() {
        return "Microservicio de clases activo";
    }
}
