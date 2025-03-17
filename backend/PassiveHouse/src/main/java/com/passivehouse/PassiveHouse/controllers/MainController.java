package com.passivehouse.PassiveHouse.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @GetMapping("/")
    public String home() {
        return "Welcome to the home page!";
    }
//
//    @GetMapping("/main")
//    public String main(@AuthenticationPrincipal OAuth2User principal) {
//        String email = principal.getAttribute("email");
//        logger.info("User '{}' accessed the main page", email);
//        return "Hello " + email + "!";
//    }
//
//    @GetMapping("/login")
//    public String login() {
//        return "Please log in with Google";
//    }
}