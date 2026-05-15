package com.nandhini.poc.applicationmanagement.service;

import com.nandhini.poc.applicationmanagement.dto.ApplicationRequestDTO;
import com.nandhini.poc.applicationmanagement.dto.ApplicationResponseDTO;

import java.util.List;

public interface ApplicationService {

    ApplicationResponseDTO createApplication(ApplicationRequestDTO requestDTO);

    ApplicationResponseDTO getApplicationById(Long id);

    List<ApplicationResponseDTO> getAllApplications();

    ApplicationResponseDTO updateApplication(Long id, ApplicationRequestDTO requestDTO);

    void deleteApplication(Long id);
}
