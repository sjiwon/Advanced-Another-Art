package com.sjiwon.anotherart.global.config.web;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
public class TimeZoneConfig {
    private static final String KOREA_TIMEZONE = "Asia/Seoul";

    @PostConstruct
    public void initTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone(KOREA_TIMEZONE));
    }
}
