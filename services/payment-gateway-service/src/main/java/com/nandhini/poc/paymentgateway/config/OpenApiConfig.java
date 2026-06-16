package com.nandhini.poc.paymentgateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI paymentGatewayOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Payment Gateway Service API")
                        .description("High-performance payment processing service with idempotency, multiple payment methods, and async processing")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Nandhini TK")
                                .url("https://github.com/NandhiniTK/spec-driven-development-spring-boot")));
    }
}
