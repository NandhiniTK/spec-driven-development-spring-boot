package com.nandhini.poc.applicationmanagement.service;

import com.nandhini.poc.applicationmanagement.dto.ApplicationRequestDTO;
import com.nandhini.poc.applicationmanagement.dto.ApplicationResponseDTO;
import com.nandhini.poc.applicationmanagement.entity.Application;
import com.nandhini.poc.applicationmanagement.entity.Environment;
import com.nandhini.poc.applicationmanagement.entity.Status;
import com.nandhini.poc.applicationmanagement.exception.DuplicateResourceException;
import com.nandhini.poc.applicationmanagement.exception.ResourceNotFoundException;
import com.nandhini.poc.applicationmanagement.mapper.ApplicationMapper;
import com.nandhini.poc.applicationmanagement.repository.ApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceImplTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ApplicationMapper applicationMapper;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    private ApplicationRequestDTO requestDTO;
    private Application application;
    private ApplicationResponseDTO responseDTO;

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

        application = Application.builder()
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
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
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
                .createdAt(application.getCreatedAt())
                .updatedAt(application.getUpdatedAt())
                .build();
    }

    @Nested
    @DisplayName("createApplication")
    class CreateApplication {

        @Test
        @DisplayName("Should create application successfully")
        void createApplication_Success() {
            when(applicationRepository.existsByName("payment-service")).thenReturn(false);
            when(applicationMapper.toEntity(requestDTO)).thenReturn(application);
            when(applicationRepository.save(application)).thenReturn(application);
            when(applicationMapper.toResponseDTO(application)).thenReturn(responseDTO);

            ApplicationResponseDTO result = applicationService.createApplication(requestDTO);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("payment-service");
            assertThat(result.getStatus()).isEqualTo(Status.ACTIVE);
            assertThat(result.getEnvironment()).isEqualTo(Environment.PROD);
            assertThat(result.getMetadata()).containsEntry("team", "payments");

            verify(applicationRepository).existsByName("payment-service");
            verify(applicationMapper).toEntity(requestDTO);
            verify(applicationRepository).save(application);
            verify(applicationMapper).toResponseDTO(application);
        }

        @Test
        @DisplayName("Should throw DuplicateResourceException when name exists")
        void createApplication_DuplicateName() {
            when(applicationRepository.existsByName("payment-service")).thenReturn(true);

            assertThatThrownBy(() -> applicationService.createApplication(requestDTO))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessageContaining("payment-service");

            verify(applicationRepository).existsByName("payment-service");
            verify(applicationRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getApplicationById")
    class GetApplicationById {

        @Test
        @DisplayName("Should return application when found")
        void getApplicationById_Success() {
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
            when(applicationMapper.toResponseDTO(application)).thenReturn(responseDTO);

            ApplicationResponseDTO result = applicationService.getApplicationById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("payment-service");

            verify(applicationRepository).findById(1L);
            verify(applicationMapper).toResponseDTO(application);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when not found")
        void getApplicationById_NotFound() {
            when(applicationRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> applicationService.getApplicationById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");

            verify(applicationRepository).findById(99L);
            verify(applicationMapper, never()).toResponseDTO(any());
        }
    }

    @Nested
    @DisplayName("getAllApplications")
    class GetAllApplications {

        @Test
        @DisplayName("Should return list of applications")
        void getAllApplications_Success() {
            when(applicationRepository.findAll()).thenReturn(List.of(application));
            when(applicationMapper.toResponseDTOList(List.of(application))).thenReturn(List.of(responseDTO));

            List<ApplicationResponseDTO> result = applicationService.getAllApplications();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("payment-service");

            verify(applicationRepository).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no applications")
        void getAllApplications_EmptyList() {
            when(applicationRepository.findAll()).thenReturn(Collections.emptyList());
            when(applicationMapper.toResponseDTOList(Collections.emptyList())).thenReturn(Collections.emptyList());

            List<ApplicationResponseDTO> result = applicationService.getAllApplications();

            assertThat(result).isEmpty();

            verify(applicationRepository).findAll();
        }
    }

    @Nested
    @DisplayName("updateApplication")
    class UpdateApplication {

        @Test
        @DisplayName("Should update application successfully")
        void updateApplication_Success() {
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
            when(applicationRepository.existsByNameAndIdNot("payment-service", 1L)).thenReturn(false);
            when(applicationRepository.save(application)).thenReturn(application);
            when(applicationMapper.toResponseDTO(application)).thenReturn(responseDTO);

            ApplicationResponseDTO result = applicationService.updateApplication(1L, requestDTO);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);

            verify(applicationRepository).findById(1L);
            verify(applicationRepository).existsByNameAndIdNot("payment-service", 1L);
            verify(applicationMapper).updateEntityFromDTO(requestDTO, application);
            verify(applicationRepository).save(application);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when not found")
        void updateApplication_NotFound() {
            when(applicationRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> applicationService.updateApplication(99L, requestDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");

            verify(applicationRepository).findById(99L);
            verify(applicationRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw DuplicateResourceException when name taken by another")
        void updateApplication_DuplicateName() {
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
            when(applicationRepository.existsByNameAndIdNot("payment-service", 1L)).thenReturn(true);

            assertThatThrownBy(() -> applicationService.updateApplication(1L, requestDTO))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessageContaining("payment-service");

            verify(applicationRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deleteApplication")
    class DeleteApplication {

        @Test
        @DisplayName("Should delete application successfully")
        void deleteApplication_Success() {
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));

            applicationService.deleteApplication(1L);

            verify(applicationRepository).findById(1L);
            verify(applicationRepository).delete(application);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when not found")
        void deleteApplication_NotFound() {
            when(applicationRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> applicationService.deleteApplication(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");

            verify(applicationRepository).findById(99L);
            verify(applicationRepository, never()).delete(any());
        }
    }
}
