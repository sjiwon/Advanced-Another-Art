package com.sjiwon.anotherart.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
    private static final String API_NAME = "Advanced Another Art API 문서";
    private static final String API_DESCRIPTION = "Advanced Another Art Rest API 명세서";
    private static final String CONTACT_NAME = "[Advanced Another Art]";
    private static final String CONTACT_URL = "https://github.com/sjiwon/advanced-another-art";
    private static final String CONTACT_EMAIL = "sjiwon4491@gmail.com";

    @Bean
    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(API_NAME)
                .description(API_DESCRIPTION)
                .contact(new Contact(CONTACT_NAME, CONTACT_URL, CONTACT_EMAIL))
                .build();
    }

    @Bean
    public Docket swagger() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.sjiwon.anotherart"))
                .paths(PathSelectors.any())
                .build();
    }
}
