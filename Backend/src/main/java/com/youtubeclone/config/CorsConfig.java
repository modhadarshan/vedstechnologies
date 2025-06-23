package com.youtubeclone.config;

// On your Spring Boot backend, create a configuration class, e.g., CorsConfig.java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**") // Apply CORS to all /api endpoints
                        .allowedOrigins("http://localhost:4200") // Replace with your Angular frontend's URL
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow common HTTP methods
                        .allowedHeaders("*") // Allow all headers
                        .allowCredentials(true) // Allow sending cookies/auth headers (e.g., X-User-Email, X-User-Password from your AuthService)
                        .maxAge(3600); // Max age of the pre-flight request result
            }
        };
    }
}