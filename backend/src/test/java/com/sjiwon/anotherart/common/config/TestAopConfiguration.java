package com.sjiwon.anotherart.common.config;

import com.sjiwon.anotherart.global.logging.LoggingStatusManager;
import com.sjiwon.anotherart.global.logging.LoggingTracer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@TestConfiguration
@EnableAspectJAutoProxy
public class TestAopConfiguration {
    @Bean
    public LoggingStatusManager loggingStatusManager() {
        return new LoggingStatusManager();
    }

    @Bean
    public LoggingTracer loggingTracer() {
        return new LoggingTracer(loggingStatusManager());
    }
}
