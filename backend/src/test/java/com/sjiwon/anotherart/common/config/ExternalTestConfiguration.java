package com.sjiwon.anotherart.common.config;

import com.sjiwon.anotherart.member.utils.MemberDoubleChecker;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class ExternalTestConfiguration {
    @Bean
    MemberDoubleChecker memberDoubleChecker() {
        return new MemberDoubleChecker();
    }
}
