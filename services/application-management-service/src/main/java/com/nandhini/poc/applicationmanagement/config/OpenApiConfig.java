package com.nandhini.poc.applicationmanagement.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI applicationManagementOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Application Management API")
                        .description("REST API for managing application metadata and configuration. "
                                + "Provides CRUD operations for application resources.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Nandhini")
                                .url("https://github.com/nandhini")));
    }
}
