package com.lending.app.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI lendingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Lending Application API")
                        .description("RESTful API for loan management")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Lending API Team")
                                .email("api@lending.app"))
                        .license(new License()
                                .name("MIT License")));
    }
}
