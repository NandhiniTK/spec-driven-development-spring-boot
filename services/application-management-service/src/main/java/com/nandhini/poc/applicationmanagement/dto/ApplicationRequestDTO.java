package com.nandhini.poc.applicationmanagement.dto;

import com.nandhini.poc.applicationmanagement.entity.Environment;
import com.nandhini.poc.applicationmanagement.entity.Status;
import com.nandhini.poc.applicationmanagement.validation.ValidMetadata;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request payload for creating or updating an application")
public class ApplicationRequestDTO {

    @Schema(description = "Unique application name", example = "payment-service", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Name must contain only alphanumeric characters, hyphens, and underscores")
    private String name;

    @Schema(description = "Application description", example = "Payment processing microservice")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Schema(description = "Semantic version", example = "1.0.0", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Version is required")
    @Pattern(regexp = "^\\d+\\.\\d+\\.\\d+$", message = "Version must follow semantic versioning (x.y.z)")
    private String version;

    @Schema(description = "Application status", example = "ACTIVE", defaultValue = "ACTIVE")
    @Builder.Default
    private Status status = Status.ACTIVE;

    @Schema(description = "Application owner", example = "john.doe", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Owner is required")
    @Size(min = 1, max = 100, message = "Owner must be between 1 and 100 characters")
    private String owner;

    @Schema(description = "Technology stack", example = "Java Spring Boot")
    @Size(max = 50, message = "Technology must not exceed 50 characters")
    private String technology;

    @Schema(description = "Deployment environment", example = "PROD", defaultValue = "DEV")
    @Builder.Default
    private Environment environment = Environment.DEV;

    @Schema(description = "Application URL", example = "https://payment.example.com")
    @org.hibernate.validator.constraints.URL(message = "URL must be a valid URL")
    private String url;

    @Schema(description = "Custom key-value metadata (max 20 pairs)", example = "{\"team\": \"payments\", \"cost-center\": \"CC-1234\"}")
    @ValidMetadata
    private Map<String, String> metadata;
}
