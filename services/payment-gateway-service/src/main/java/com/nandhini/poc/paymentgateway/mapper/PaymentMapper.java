package com.nandhini.poc.paymentgateway.mapper;

import com.nandhini.poc.paymentgateway.dto.PaymentRequestDTO;
import com.nandhini.poc.paymentgateway.dto.PaymentResponseDTO;
import com.nandhini.poc.paymentgateway.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "gatewayTransactionId", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Payment toEntity(PaymentRequestDTO requestDTO);

    PaymentResponseDTO toResponseDTO(Payment payment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "gatewayTransactionId", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(PaymentRequestDTO requestDTO, @MappingTarget Payment payment);
}
