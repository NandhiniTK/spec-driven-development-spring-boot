package com.nandhini.poc.applicationmanagement.mapper;

import com.nandhini.poc.applicationmanagement.dto.ApplicationRequestDTO;
import com.nandhini.poc.applicationmanagement.dto.ApplicationResponseDTO;
import com.nandhini.poc.applicationmanagement.entity.Application;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Application toEntity(ApplicationRequestDTO dto);

    ApplicationResponseDTO toResponseDTO(Application application);

    List<ApplicationResponseDTO> toResponseDTOList(List<Application> applications);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(ApplicationRequestDTO dto, @MappingTarget Application application);
}
