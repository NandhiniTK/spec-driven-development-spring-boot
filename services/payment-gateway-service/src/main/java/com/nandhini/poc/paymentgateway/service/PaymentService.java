package com.nandhini.poc.paymentgateway.service;

import com.nandhini.poc.paymentgateway.dto.PaymentRequestDTO;
import com.nandhini.poc.paymentgateway.dto.PaymentResponseDTO;
import com.nandhini.poc.paymentgateway.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PaymentService {
    
    PaymentResponseDTO initiatePayment(String idempotencyKey, PaymentRequestDTO requestDTO);
    
    PaymentResponseDTO getPaymentById(UUID id);
    
    Page<PaymentResponseDTO> getAllPayments(PaymentStatus status, Pageable pageable);
}
