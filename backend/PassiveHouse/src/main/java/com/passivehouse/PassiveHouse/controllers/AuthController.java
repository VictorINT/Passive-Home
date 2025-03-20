package com.passivehouse.PassiveHouse.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @GetMapping("/unauthorized")
    public String unauthorized() {
        return "You are not authorized to access this application. Please contact the administrator.";
    }
} 