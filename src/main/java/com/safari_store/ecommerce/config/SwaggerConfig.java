package com.safari_store.ecommerce.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8081}")
    private String serverPort;

    @Bean
    public OpenAPI safariStoreOpenAPI(){
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .name("Authorization")
                .description("Enter JWT Bearer Token");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("bearerAuth");


        return new OpenAPI()
                .info(new Info()
                        .title("Safari Store API")
                        .description("RESTful API documentation for Safari Store - An eCommerce platform built with Spring Boot")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Safari Store Development Team")
                                .email("ondeyostephen0@gmail.com")
                                .url("https://github.com/yourusername/safari-store"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://127.0.0.1:" + serverPort)
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.safaristore.com")
                                .description("Production Server")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", securityScheme))
                .addSecurityItem(securityRequirement);
    }
}
