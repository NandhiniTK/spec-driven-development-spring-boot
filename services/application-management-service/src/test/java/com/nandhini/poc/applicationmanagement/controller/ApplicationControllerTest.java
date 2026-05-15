package com.nandhini.poc.applicationmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nandhini.poc.applicationmanagement.dto.ApplicationRequestDTO;
import com.nandhini.poc.applicationmanagement.dto.ApplicationResponseDTO;
import com.nandhini.poc.applicationmanagement.entity.Environment;
import com.nandhini.poc.applicationmanagement.entity.Status;
import com.nandhini.poc.applicationmanagement.exception.DuplicateResourceException;
import com.nandhini.poc.applicationmanagement.exception.GlobalExceptionHandler;
import com.nandhini.poc.applicationmanagement.exception.ResourceNotFoundException;
import com.nandhini.poc.applicationmanagement.service.ApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApplicationController.class)
@DisplayName("ApplicationController Tests")
class ApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ApplicationService applicationService;

    private ApplicationRequestDTO requestDTO;
    private ApplicationResponseDTO responseDTO;

    private static final String BASE_URL = "/api/v1/applications";

    @BeforeEach
    void setUp() {
        requestDTO = ApplicationRequestDTO.builder()
                .name("payment-service")
                .description("Payment processing microservice")
                .version("1.0.0")
                .status(Status.ACTIVE)
                .owner("john.doe")
                .technology("Java Spring Boot")
                .environment(Environment.PROD)
                .url("https://payment.example.com")
                .metadata(Map.of("team", "payments"))
                .build();

        responseDTO = ApplicationResponseDTO.builder()
                .id(1L)
                .name("payment-service")
                .description("Payment processing microservice")
                .version("1.0.0")
                .status(Status.ACTIVE)
                .owner("john.doe")
                .technology("Java Spring Boot")
                .environment(Environment.PROD)
                .url("https://payment.example.com")
                .metadata(Map.of("team", "payments"))
                .createdAt(LocalDateTime.of(2026, 5, 15, 10, 0, 0))
                .updatedAt(LocalDateTime.of(2026, 5, 15, 10, 0, 0))
                .build();
    }

    @Nested
    @DisplayName("POST /api/v1/applications")
    class CreateApplication {

        @Test
        @DisplayName("Should return 201 when application created successfully")
        void createApplication_Success() throws Exception {
            when(applicationService.createApplication(any(ApplicationRequestDTO.class))).thenReturn(responseDTO);

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("payment-service")))
                    .andExpect(jsonPath("$.status", is("ACTIVE")))
                    .andExpect(jsonPath("$.environment", is("PROD")))
                    .andExpect(jsonPath("$.metadata.team", is("payments")));

            verify(applicationService).createApplication(any(ApplicationRequestDTO.class));
        }

        @Test
        @DisplayName("Should return 400 when name is blank")
        void createApplication_BlankName() throws Exception {
            requestDTO.setName("");

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.error", is("Bad Request")));

            verify(applicationService, never()).createApplication(any());
        }

        @Test
        @DisplayName("Should return 400 when version format is invalid")
        void createApplication_InvalidVersion() throws Exception {
            requestDTO.setVersion("invalid");

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", containsString("version")));

            verify(applicationService, never()).createApplication(any());
        }

        @Test
        @DisplayName("Should return 400 when owner is missing")
        void createApplication_MissingOwner() throws Exception {
            requestDTO.setOwner(null);

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isBadRequest());

            verify(applicationService, never()).createApplication(any());
        }

        @Test
        @DisplayName("Should return 400 when name contains invalid characters")
        void createApplication_InvalidNameChars() throws Exception {
            requestDTO.setName("invalid name!");

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", containsString("name")));

            verify(applicationService, never()).createApplication(any());
        }

        @Test
        @DisplayName("Should return 400 when application name already exists")
        void createApplication_DuplicateName() throws Exception {
            when(applicationService.createApplication(any(ApplicationRequestDTO.class)))
                    .thenThrow(new DuplicateResourceException("Application with name 'payment-service' already exists"));

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", containsString("already exists")));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/applications/{id}")
    class GetApplicationById {

        @Test
        @DisplayName("Should return 200 with application")
        void getApplicationById_Success() throws Exception {
            when(applicationService.getApplicationById(1L)).thenReturn(responseDTO);

            mockMvc.perform(get(BASE_URL + "/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("payment-service")))
                    .andExpect(jsonPath("$.owner", is("john.doe")));

            verify(applicationService).getApplicationById(1L);
        }

        @Test
        @DisplayName("Should return 404 when application not found")
        void getApplicationById_NotFound() throws Exception {
            when(applicationService.getApplicationById(99L))
                    .thenThrow(new ResourceNotFoundException("Application not found with id: 99"));

            mockMvc.perform(get(BASE_URL + "/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.message", containsString("99")));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/applications")
    class GetAllApplications {

        @Test
        @DisplayName("Should return 200 with list of applications")
        void getAllApplications_Success() throws Exception {
            when(applicationService.getAllApplications()).thenReturn(List.of(responseDTO));

            mockMvc.perform(get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].name", is("payment-service")));

            verify(applicationService).getAllApplications();
        }

        @Test
        @DisplayName("Should return 200 with empty list")
        void getAllApplications_EmptyList() throws Exception {
            when(applicationService.getAllApplications()).thenReturn(Collections.emptyList());

            mockMvc.perform(get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/applications/{id}")
    class UpdateApplication {

        @Test
        @DisplayName("Should return 200 when application updated successfully")
        void updateApplication_Success() throws Exception {
            when(applicationService.updateApplication(eq(1L), any(ApplicationRequestDTO.class))).thenReturn(responseDTO);

            mockMvc.perform(put(BASE_URL + "/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("payment-service")));

            verify(applicationService).updateApplication(eq(1L), any(ApplicationRequestDTO.class));
        }

        @Test
        @DisplayName("Should return 404 when application not found")
        void updateApplication_NotFound() throws Exception {
            when(applicationService.updateApplication(eq(99L), any(ApplicationRequestDTO.class)))
                    .thenThrow(new ResourceNotFoundException("Application not found with id: 99"));

            mockMvc.perform(put(BASE_URL + "/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", containsString("99")));
        }

        @Test
        @DisplayName("Should return 400 when validation fails")
        void updateApplication_ValidationError() throws Exception {
            requestDTO.setName("ab");

            mockMvc.perform(put(BASE_URL + "/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", containsString("Name")));

            verify(applicationService, never()).updateApplication(anyLong(), any());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/applications/{id}")
    class DeleteApplication {

        @Test
        @DisplayName("Should return 204 when application deleted")
        void deleteApplication_Success() throws Exception {
            doNothing().when(applicationService).deleteApplication(1L);

            mockMvc.perform(delete(BASE_URL + "/1"))
                    .andExpect(status().isNoContent());

            verify(applicationService).deleteApplication(1L);
        }

        @Test
        @DisplayName("Should return 404 when application not found")
        void deleteApplication_NotFound() throws Exception {
            doThrow(new ResourceNotFoundException("Application not found with id: 99"))
                    .when(applicationService).deleteApplication(99L);

            mockMvc.perform(delete(BASE_URL + "/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", containsString("99")));
        }
    }
}
