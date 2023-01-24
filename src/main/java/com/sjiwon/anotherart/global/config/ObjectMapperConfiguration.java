package com.sjiwon.anotherart.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfiguration {
    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
