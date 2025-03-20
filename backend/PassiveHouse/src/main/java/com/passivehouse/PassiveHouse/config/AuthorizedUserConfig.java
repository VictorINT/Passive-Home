package com.passivehouse.PassiveHouse.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthorizedUserConfig {
    
    @Value("${authorized.user.email:}")
    private String authorizedEmail;
    
    public String getAuthorizedEmail() {
        return authorizedEmail;
    }
} 