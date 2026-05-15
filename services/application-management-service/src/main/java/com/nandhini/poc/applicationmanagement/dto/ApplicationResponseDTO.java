package com.nandhini.poc.applicationmanagement.dto;

import com.nandhini.poc.applicationmanagement.entity.Environment;
import com.nandhini.poc.applicationmanagement.entity.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Application response payload")
public class ApplicationResponseDTO {

    @Schema(description = "Auto-generated application ID", example = "1")
    private Long id;
    private String name;
    private String description;
    private String version;
    private Status status;
    private String owner;
    private String technology;
    private Environment environment;
    private String url;
    private Map<String, String> metadata;
    @Schema(description = "Creation timestamp", example = "2026-05-15T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2026-05-15T10:00:00")
    private LocalDateTime updatedAt;
}
