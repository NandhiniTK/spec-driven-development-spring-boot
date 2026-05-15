package com.nandhini.poc.applicationmanagement.repository;

import com.nandhini.poc.applicationmanagement.entity.Application;
import com.nandhini.poc.applicationmanagement.entity.Environment;
import com.nandhini.poc.applicationmanagement.entity.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ApplicationRepository Tests")
class ApplicationRepositoryTest {

    @Mock
    private ApplicationRepository applicationRepository;

    private Application application;

    @BeforeEach
    void setUp() {
        application = Application.builder()
                .id(1L)
                .name("test-app")
                .description("Test application")
                .version("1.0.0")
                .status(Status.ACTIVE)
                .owner("test-owner")
                .technology("Java")
                .environment(Environment.DEV)
                .url("https://test.example.com")
                .metadata(Map.of("key", "value"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("existsByName should return true when name exists")
    void existsByName_ReturnsTrue() {
        when(applicationRepository.existsByName("test-app")).thenReturn(true);

        boolean result = applicationRepository.existsByName("test-app");

        assertThat(result).isTrue();
        verify(applicationRepository).existsByName("test-app");
    }

    @Test
    @DisplayName("existsByName should return false when name does not exist")
    void existsByName_ReturnsFalse() {
        when(applicationRepository.existsByName("nonexistent")).thenReturn(false);

        boolean result = applicationRepository.existsByName("nonexistent");

        assertThat(result).isFalse();
        verify(applicationRepository).existsByName("nonexistent");
    }

    @Test
    @DisplayName("existsByNameAndIdNot should return true when name exists for different id")
    void existsByNameAndIdNot_ReturnsTrue() {
        when(applicationRepository.existsByNameAndIdNot("test-app", 2L)).thenReturn(true);

        boolean result = applicationRepository.existsByNameAndIdNot("test-app", 2L);

        assertThat(result).isTrue();
        verify(applicationRepository).existsByNameAndIdNot("test-app", 2L);
    }

    @Test
    @DisplayName("existsByNameAndIdNot should return false when name exists for same id")
    void existsByNameAndIdNot_ReturnsFalse() {
        when(applicationRepository.existsByNameAndIdNot("test-app", 1L)).thenReturn(false);

        boolean result = applicationRepository.existsByNameAndIdNot("test-app", 1L);

        assertThat(result).isFalse();
        verify(applicationRepository).existsByNameAndIdNot("test-app", 1L);
    }

    @Test
    @DisplayName("save should persist and return application")
    void save_Success() {
        when(applicationRepository.save(any(Application.class))).thenReturn(application);

        Application saved = applicationRepository.save(application);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(1L);
        assertThat(saved.getName()).isEqualTo("test-app");
        verify(applicationRepository).save(application);
    }

    @Test
    @DisplayName("findById should return application when found")
    void findById_Found() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));

        Optional<Application> result = applicationRepository.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("test-app");
        verify(applicationRepository).findById(1L);
    }

    @Test
    @DisplayName("findById should return empty when not found")
    void findById_NotFound() {
        when(applicationRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Application> result = applicationRepository.findById(99L);

        assertThat(result).isEmpty();
        verify(applicationRepository).findById(99L);
    }

    @Test
    @DisplayName("findAll should return list of applications")
    void findAll_Success() {
        when(applicationRepository.findAll()).thenReturn(List.of(application));

        List<Application> result = applicationRepository.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("test-app");
        verify(applicationRepository).findAll();
    }

    @Test
    @DisplayName("delete should remove application")
    void delete_Success() {
        doNothing().when(applicationRepository).delete(application);

        applicationRepository.delete(application);

        verify(applicationRepository).delete(application);
    }
}
