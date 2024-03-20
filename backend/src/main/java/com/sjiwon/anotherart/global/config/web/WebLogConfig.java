package com.sjiwon.anotherart.global.config.web;

import com.sjiwon.anotherart.global.filter.MdcLoggingFilter;
import com.sjiwon.anotherart.global.filter.RequestLoggingFilter;
import com.sjiwon.anotherart.global.filter.RequestResponseCachingFilter;
import com.sjiwon.anotherart.global.log.LoggingStatusManager;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebLogConfig {
    @Bean
    public FilterRegistrationBean<MdcLoggingFilter> firstFilter() {
        final FilterRegistrationBean<MdcLoggingFilter> bean = new FilterRegistrationBean<>();
        bean.setOrder(1);
        bean.setFilter(new MdcLoggingFilter());
        bean.setName("mdcLoggingFilter");
        bean.addUrlPatterns("/api/*");
        return bean;
    }

    @Bean
    public FilterRegistrationBean<RequestResponseCachingFilter> secondFilter() {
        final FilterRegistrationBean<RequestResponseCachingFilter> bean = new FilterRegistrationBean<>();
        bean.setOrder(2);
        bean.setFilter(new RequestResponseCachingFilter());
        bean.setName("requestResponseCachingFilter");
        bean.addUrlPatterns("/api/*");
        return bean;
    }

    @Bean
    public FilterRegistrationBean<RequestLoggingFilter> thirdFilter(
            final LoggingStatusManager loggingStatusManager
    ) {
        final FilterRegistrationBean<RequestLoggingFilter> bean = new FilterRegistrationBean<>();
        bean.setOrder(3);
        bean.setFilter(new RequestLoggingFilter(
                loggingStatusManager,
                "/favicon.ico",
                "/error*",
                "/api/swagger*",
                "/api-docs*",
                "/api/actuator*",
                "/api/health"
        ));
        bean.setName("requestLoggingFilter");
        bean.addUrlPatterns("/api/*");
        return bean;
    }
}
