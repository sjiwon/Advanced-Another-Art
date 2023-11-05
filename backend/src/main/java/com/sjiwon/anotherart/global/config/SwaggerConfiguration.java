package com.sjiwon.anotherart.global.config;

import com.sjiwon.anotherart.global.resolver.ExtractPayload;
import com.sjiwon.anotherart.global.resolver.ExtractToken;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Another Art API 명세서",
                description = "Another Art API 명세서",
                contact = @Contact(
                        name = "서지원",
                        url = "https://github.com/sjiwon/Advanced-Another-Art",
                        email = "sjiwo4491@gmail.com"
                ),
                version = "v1"
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080",
                        description = "Local Server"
                )
        }
)
public class SwaggerConfiguration {
    static {
        SpringDocUtils
                .getConfig()
                .addAnnotationsToIgnore(
                        ExtractToken.class,
                        ExtractPayload.class
                );
    }

    @Bean
    public GroupedOpenApi studyWithMeApi() {
        return GroupedOpenApi.builder()
                .group("Another Art API 명세서")
                .pathsToMatch(
                        "/api/**"
                )
                .addOpenApiCustomizer(buildSecurityOpenApi())
                .build();
    }

    private OpenApiCustomizer buildSecurityOpenApi() {
        return openApi -> openApi
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList("JWT Token")
                )
                .getComponents()
                .addSecuritySchemes(
                        "JWT Token",
                        new SecurityScheme()
                                .name(AUTHORIZATION)
                                .type(SecurityScheme.Type.HTTP)
                                .in(SecurityScheme.In.HEADER)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                );
    }
}
