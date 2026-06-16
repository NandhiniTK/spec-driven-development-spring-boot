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
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "spring.cloud.aws.sqs.enabled", havingValue = "true", matchIfMissing = false)
public class PaymentProcessorService {

    private final PaymentRepository paymentRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final List<PaymentMethodHandler> paymentMethodHandlers;
    private final ObjectMapper objectMapper;
    private final Counter paymentSuccessCounter;
    private final Counter paymentFailureCounter;
    private final Counter paymentTimeoutCounter;
    private final Timer paymentProcessingTimer;

    private Map<com.nandhini.poc.paymentgateway.entity.PaymentMethod, PaymentMethodHandler> handlerMap;

    @SqsListener("${payment.queue.name}")
    @Transactional
    public void processPaymentMessage(String message) {
        log.info("Received payment message from SQS: {}", message);
        
        try {
            // Parse message
            PaymentMessageDTO messageDTO = objectMapper.readValue(message, PaymentMessageDTO.class);
            
            // Restore correlation ID for tracing
            if (messageDTO.getCorrelationId() != null) {
                org.slf4j.MDC.put("correlationId", messageDTO.getCorrelationId());
            }
            
            log.info("Processing payment: paymentId={}", messageDTO.getPaymentId());
            
            // Start timer for metrics
            Timer.Sample sample = Timer.start();
            
            // Fetch payment with pessimistic lock (SELECT FOR UPDATE)
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
                
                // Record success metric
                paymentSuccessCounter.increment();
                sample.stop(paymentProcessingTimer);
            } else {
                // Check if it's a timeout (circuit breaker fallback)
                if (payment.getGatewayTransactionId() == null) {
                    payment.setStatus(PaymentStatus.TIMEOUT);
                    paymentRepository.save(payment);
                    logPaymentEvent(payment.getId(), "PAYMENT_TIMEOUT", 
                            "Payment gateway timeout - circuit breaker opened or retries exhausted");
                    log.error("Payment timeout: paymentId={}", payment.getId());
                    
                    // Record timeout metric
                    paymentTimeoutCounter.increment();
                } else {
                    payment.setStatus(PaymentStatus.FAILED);
                    paymentRepository.save(payment);
                    logPaymentEvent(payment.getId(), "PAYMENT_FAILED", 
                            "Payment processing failed");
                    log.error("Payment processing failed: paymentId={}", payment.getId());
                    
                    // Record failure metric
                    paymentFailureCounter.increment();
                }
                
                // Throw exception to trigger SQS retry/DLQ
                throw new PaymentProcessingException("Payment processing failed for paymentId: " + payment.getId());
            }
            
        } catch (PaymentProcessingException e) {
            log.error("Payment processing exception: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error processing payment message", e);
            throw new PaymentProcessingException("Unexpected error processing payment", e);
        } finally {
            org.slf4j.MDC.remove("correlationId");
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
        }
    }
}
