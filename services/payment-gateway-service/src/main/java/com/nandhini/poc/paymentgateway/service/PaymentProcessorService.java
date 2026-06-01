package com.nandhini.poc.paymentgateway.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nandhini.poc.paymentgateway.dto.PaymentMessageDTO;
import com.nandhini.poc.paymentgateway.entity.Payment;
import com.nandhini.poc.paymentgateway.entity.PaymentStatus;
import com.nandhini.poc.paymentgateway.entity.PaymentTransaction;
import com.nandhini.poc.paymentgateway.exception.PaymentProcessingException;
import com.nandhini.poc.paymentgateway.repository.PaymentRepository;
import com.nandhini.poc.paymentgateway.repository.PaymentTransactionRepository;
import com.nandhini.poc.paymentgateway.service.handler.PaymentMethodHandler;
import io.awspring.cloud.sqs.annotation.SqsListener;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentProcessorService {

    private final PaymentRepository paymentRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final List<PaymentMethodHandler> paymentMethodHandlers;
    private final ObjectMapper objectMapper;

    private Map<com.nandhini.poc.paymentgateway.entity.PaymentMethod, PaymentMethodHandler> handlerMap;

    @SqsListener("${payment.queue.name}")
    @Transactional
    public void processPaymentMessage(String message) {
        log.info("Received payment message from SQS: {}", message);
        
        try {
            // Parse message
            PaymentMessageDTO messageDTO = objectMapper.readValue(message, PaymentMessageDTO.class);
            log.info("Processing payment: paymentId={}", messageDTO.getPaymentId());
            
            // Fetch payment with pessimistic lock (SELECT FOR UPDATE)
            // This prevents concurrent processing by blocking other consumers until transaction commits
            Payment payment = paymentRepository.findByIdWithLock(messageDTO.getPaymentId())
                    .orElseThrow(() -> new PaymentProcessingException(
                            "Payment not found: " + messageDTO.getPaymentId()));
            
            // Validate payment status - skip if already processed
            if (payment.getStatus() != PaymentStatus.PENDING) {
                log.warn("Payment already processed: paymentId={}, status={}", 
                        payment.getId(), payment.getStatus());
                logPaymentEvent(payment.getId(), "PAYMENT_ALREADY_PROCESSED", 
                        "Payment status: " + payment.getStatus());
                return;
            }
            
            // Update status to PROCESSING
            payment.setStatus(PaymentStatus.PROCESSING);
            paymentRepository.save(payment);
            logPaymentEvent(payment.getId(), "PAYMENT_PROCESSING", 
                    "Payment processing started");
            
            // Get appropriate handler
            PaymentMethodHandler handler = getHandler(payment.getPaymentMethod());
            
            // Process payment
            boolean success = handler.processPayment(payment);
            
            // Update payment status based on result
            if (success) {
                payment.setStatus(PaymentStatus.SUCCESS);
                paymentRepository.save(payment);
                logPaymentEvent(payment.getId(), "PAYMENT_SUCCESS", 
                        "Payment processed successfully. Gateway Transaction ID: " + payment.getGatewayTransactionId());
                log.info("Payment processed successfully: paymentId={}", payment.getId());
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
                logPaymentEvent(payment.getId(), "PAYMENT_FAILED", 
                        "Payment processing failed");
                log.error("Payment processing failed: paymentId={}", payment.getId());
                
                // Throw exception to trigger SQS retry/DLQ
                throw new PaymentProcessingException("Payment processing failed for paymentId: " + payment.getId());
            }
            
        } catch (PaymentProcessingException e) {
            log.error("Payment processing exception: {}", e.getMessage());
            throw e; // Re-throw to trigger SQS retry/DLQ
        } catch (Exception e) {
            log.error("Unexpected error processing payment message", e);
            throw new PaymentProcessingException("Unexpected error processing payment", e);
        }
    }

    private PaymentMethodHandler getHandler(com.nandhini.poc.paymentgateway.entity.PaymentMethod paymentMethod) {
        if (handlerMap == null) {
            handlerMap = paymentMethodHandlers.stream()
                    .collect(Collectors.toMap(
                            PaymentMethodHandler::getSupportedPaymentMethod,
                            Function.identity()
                    ));
        }
        
        PaymentMethodHandler handler = handlerMap.get(paymentMethod);
        if (handler == null) {
            throw new PaymentProcessingException("No handler found for payment method: " + paymentMethod);
        }
        
        return handler;
    }

    private void logPaymentEvent(java.util.UUID paymentId, String eventType, String eventData) {
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
}
