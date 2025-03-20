package com.passivehouse.PassiveHouse.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String test(@AuthenticationPrincipal OAuth2User principal) {
        if (principal != null) {
            return "Welcome " + principal.getAttribute("email") + "! You are authenticated.";
        }
        return "Welcome! Please <a href='/oauth2/authorization/google'>login</a>.";
    }
} 