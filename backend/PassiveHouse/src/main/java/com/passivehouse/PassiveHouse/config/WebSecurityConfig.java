package com.passivehouse.PassiveHouse.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/ws/sensors").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable()); // Dezactivează CSRF pentru WebSocket
        return http.build();
    }
}