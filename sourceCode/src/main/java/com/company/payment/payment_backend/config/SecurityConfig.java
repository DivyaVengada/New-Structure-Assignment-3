package com.company.payment.payment_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@org.springframework.context.annotation.Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(csrf -> csrf.disable()).authorizeHttpRequests(request -> request.
                requestMatchers("/api/payment/**", "/api/subscription/**", "/api/webhook/**").permitAll()
                .anyRequest().authenticated());

        return httpSecurity.build();
    }
}
