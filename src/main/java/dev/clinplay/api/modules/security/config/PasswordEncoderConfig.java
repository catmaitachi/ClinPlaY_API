package dev.clinplay.api.modules.security.config;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class PasswordEncoderConfig {

    @Value("${security.secret}") private String secret;

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(10, new SecureRandom(secret.getBytes())); }
    
}