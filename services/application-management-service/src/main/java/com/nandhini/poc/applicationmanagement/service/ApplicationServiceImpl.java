package com.nandhini.poc.applicationmanagement.service;

import com.nandhini.poc.applicationmanagement.dto.ApplicationRequestDTO;
import com.nandhini.poc.applicationmanagement.dto.ApplicationResponseDTO;
import com.nandhini.poc.applicationmanagement.entity.Application;
import com.nandhini.poc.applicationmanagement.exception.DuplicateResourceException;
import com.nandhini.poc.applicationmanagement.exception.ResourceNotFoundException;
import com.nandhini.poc.applicationmanagement.mapper.ApplicationMapper;
import com.nandhini.poc.applicationmanagement.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationMapper applicationMapper;

    @Override
    @Transactional
    public ApplicationResponseDTO createApplication(ApplicationRequestDTO requestDTO) {
        log.info("Creating application with name: {}", requestDTO.getName());

        if (applicationRepository.existsByName(requestDTO.getName())) {
            throw new DuplicateResourceException("Application with name '" + requestDTO.getName() + "' already exists");
        }

        Application application = applicationMapper.toEntity(requestDTO);
        Application saved = applicationRepository.save(application);

        log.info("Application created with id: {}", saved.getId());
        return applicationMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationResponseDTO getApplicationById(Long id) {
        log.info("Fetching application with id: {}", id);

        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));

        return applicationMapper.toResponseDTO(application);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponseDTO> getAllApplications() {
        log.info("Fetching all applications");

        List<Application> applications = applicationRepository.findAll();
        return applicationMapper.toResponseDTOList(applications);
    }

    @Override
    @Transactional
    public ApplicationResponseDTO updateApplication(Long id, ApplicationRequestDTO requestDTO) {
        log.info("Updating application with id: {}", id);

        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));

        if (applicationRepository.existsByNameAndIdNot(requestDTO.getName(), id)) {
            throw new DuplicateResourceException("Application with name '" + requestDTO.getName() + "' already exists");
        }

        applicationMapper.updateEntityFromDTO(requestDTO, application);
        Application updated = applicationRepository.save(application);

        log.info("Application updated with id: {}", updated.getId());
        return applicationMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional
    public void deleteApplication(Long id) {
        log.info("Deleting application with id: {}", id);

        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));

        applicationRepository.delete(application);
        log.info("Application deleted with id: {}", id);
    }
}
