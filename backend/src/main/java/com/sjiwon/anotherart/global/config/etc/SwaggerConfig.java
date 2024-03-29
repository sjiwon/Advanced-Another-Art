package com.sjiwon.anotherart.global.config.etc;

import com.sjiwon.anotherart.global.annotation.Auth;
import com.sjiwon.anotherart.global.annotation.ExtractToken;
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
                        email = "sjiwon4491@gmail.com"
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
public class SwaggerConfig {
    static {
        SpringDocUtils
                .getConfig()
                .addAnnotationsToIgnore(
                        Auth.class,
                        ExtractToken.class
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
