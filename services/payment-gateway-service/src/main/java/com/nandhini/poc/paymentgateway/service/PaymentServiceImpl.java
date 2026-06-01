package com.nandhini.poc.paymentgateway.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nandhini.poc.paymentgateway.dto.PaymentMessageDTO;
import com.nandhini.poc.paymentgateway.dto.PaymentRequestDTO;
import com.nandhini.poc.paymentgateway.dto.PaymentResponseDTO;
import com.nandhini.poc.paymentgateway.entity.IdempotencyKey;
import com.nandhini.poc.paymentgateway.entity.Payment;
import com.nandhini.poc.paymentgateway.entity.PaymentStatus;
import com.nandhini.poc.paymentgateway.entity.PaymentTransaction;
import com.nandhini.poc.paymentgateway.exception.DuplicatePaymentException;
import com.nandhini.poc.paymentgateway.exception.InvalidPaymentException;
import com.nandhini.poc.paymentgateway.exception.PaymentNotFoundException;
import com.nandhini.poc.paymentgateway.mapper.PaymentMapper;
import com.nandhini.poc.paymentgateway.repository.IdempotencyKeyRepository;
import com.nandhini.poc.paymentgateway.repository.PaymentRepository;
import com.nandhini.poc.paymentgateway.repository.PaymentTransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentMapper paymentMapper;
    
    @Autowired(required = false)
    private SQSMessagePublisher sqsMessagePublisher;
    
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    
    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              IdempotencyKeyRepository idempotencyKeyRepository,
                              PaymentTransactionRepository paymentTransactionRepository,
                              PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.idempotencyKeyRepository = idempotencyKeyRepository;
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.paymentMapper = paymentMapper;
    }

    @Override
    @Transactional
    public PaymentResponseDTO initiatePayment(String idempotencyKey, PaymentRequestDTO requestDTO) {
        log.info("Initiating payment with idempotency key: {}", idempotencyKey);
        
        // Check for existing idempotency key
        return idempotencyKeyRepository.findByKey(idempotencyKey)
                .map(this::getCachedResponse)
                .orElseGet(() -> createNewPayment(idempotencyKey, requestDTO));
    }

    private PaymentResponseDTO getCachedResponse(IdempotencyKey idempotencyKey) {
        log.info("Returning cached response for idempotency key: {}", idempotencyKey.getKey());
        
        try {
            return objectMapper.readValue(idempotencyKey.getResponseBody(), PaymentResponseDTO.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize cached response", e);
            throw new InvalidPaymentException("Failed to retrieve cached payment response");
        }
    }

    private PaymentResponseDTO createNewPayment(String idempotencyKey, PaymentRequestDTO requestDTO) {
        // Validate request
        validatePaymentRequest(requestDTO);
        
        // Create payment entity
        Payment payment = paymentMapper.toEntity(requestDTO);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setUserId("SYSTEM"); // TODO: Replace with actual user ID from JWT in Phase 11
        
        // Save payment
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Created payment with ID: {}", savedPayment.getId());
        
        // Log payment creation event
        logPaymentEvent(savedPayment.getId(), "PAYMENT_CREATED", 
                String.format("Payment created with amount: %s %s", savedPayment.getAmount(), savedPayment.getCurrency()));
        
        // Convert to response DTO
        PaymentResponseDTO responseDTO = paymentMapper.toResponseDTO(savedPayment);
        
        // Store idempotency key with cached response
        try {
            String responseBody = objectMapper.writeValueAsString(responseDTO);
            
            IdempotencyKey idempotencyKeyEntity = IdempotencyKey.builder()
                    .key(idempotencyKey)
                    .paymentId(savedPayment.getId())
                    .responseBody(responseBody)
                    .statusCode(HttpStatus.ACCEPTED.value())
                    .expiresAt(LocalDateTime.now().plusHours(24))
                    .build();
            
            idempotencyKeyRepository.save(idempotencyKeyEntity);
            log.info("Stored idempotency key: {}", idempotencyKey);
            
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize response for idempotency key storage", e);
            // Continue without caching - payment is already created
        }
        
        // Publish payment message to SQS for async processing (if SQS is enabled)
        if (sqsMessagePublisher != null) {
            try {
                PaymentMessageDTO messageDTO = PaymentMessageDTO.builder()
                        .paymentId(savedPayment.getId())
                        .correlationId(org.slf4j.MDC.get("correlationId"))
                        .build();
                
                sqsMessagePublisher.publishPaymentMessage(messageDTO);
                
                logPaymentEvent(savedPayment.getId(), "PAYMENT_QUEUED", 
                        "Payment message published to SQS for processing");
                
            } catch (Exception e) {
                log.error("Failed to publish payment message to SQS: paymentId={}", savedPayment.getId(), e);
                logPaymentEvent(savedPayment.getId(), "PAYMENT_QUEUE_FAILED", 
                        "Failed to publish payment message to SQS: " + e.getMessage());
                // Don't fail the request - payment is created, will be retried
            }
        } else {
            log.warn("SQS is disabled - payment will not be processed asynchronously: paymentId={}", savedPayment.getId());
        }
        
        return responseDTO;
    }
    
    private void logPaymentEvent(UUID paymentId, String eventType, String eventData) {
        try {
            PaymentTransaction transaction = PaymentTransaction.builder()
                    .paymentId(paymentId)
                    .eventType(eventType)
                    .eventData(eventData)
                    .build();
            
            paymentTransactionRepository.save(transaction);
            log.debug("Logged payment event: paymentId={}, eventType={}", paymentId, eventType);
            
        } catch (Exception e) {
            log.error("Failed to log payment event: paymentId={}, eventType={}", paymentId, eventType, e);
            // Don't fail the main operation
        }
    }

    private void validatePaymentRequest(PaymentRequestDTO requestDTO) {
        if (requestDTO.getAmount() == null || requestDTO.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new InvalidPaymentException("Amount must be greater than 0");
        }
        
        if (requestDTO.getCurrency() == null) {
            throw new InvalidPaymentException("Currency is required");
        }
        
        if (requestDTO.getPaymentMethod() == null) {
            throw new InvalidPaymentException("Payment method is required");
        }
        
        if (requestDTO.getMetadata() != null && requestDTO.getMetadata().size() > 20) {
            throw new InvalidPaymentException("Metadata must not exceed 20 key-value pairs");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentById(UUID id) {
        log.info("Fetching payment by ID: {}", id);
        
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with ID: " + id));
        
        return paymentMapper.toResponseDTO(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponseDTO> getAllPayments(PaymentStatus status, Pageable pageable) {
        log.info("Fetching payments with status: {}, page: {}", status, pageable.getPageNumber());
        
        Page<Payment> payments;
        
        if (status != null) {
            payments = paymentRepository.findByUserIdAndStatus("SYSTEM", status, pageable);
        } else {
            payments = paymentRepository.findByUserId("SYSTEM", pageable);
        }
        
        return payments.map(paymentMapper::toResponseDTO);
    }
}
