package com.nandhini.poc.paymentgateway.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nandhini.poc.paymentgateway.dto.PaymentMessageDTO;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "spring.cloud.aws.sqs.enabled", havingValue = "true", matchIfMissing = false)
public class SQSMessagePublisher {

    private final SqsTemplate sqsTemplate;
    private final ObjectMapper objectMapper;

    @Value("${payment.queue.name}")
    private String queueName;

    /**
     * Publishes payment message to SQS for async processing.
     * 
     * @Retryable handles transient SQS infrastructure failures (network issues, temporary SQS unavailability).
     * This is different from payment processing retries, which are handled by SQS itself via maxReceiveCount.
     * 
     * If all retries fail, the payment becomes orphaned (created but not queued).
     * TODO: Add scheduled job to reprocess orphaned payments (Phase 9/10).
     */
    @Retryable(
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2),
            retryFor = {Exception.class}
    ) 
    public void publishPaymentMessage(PaymentMessageDTO messageDTO) {
        try {
            String messageBody = objectMapper.writeValueAsString(messageDTO);
            
            log.info("Publishing payment message to SQS: paymentId={}", messageDTO.getPaymentId());
            
            sqsTemplate.send(queueName, messageBody);
            
            log.info("Successfully published payment message: paymentId={}", messageDTO.getPaymentId());
            
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize payment message: paymentId={}", messageDTO.getPaymentId(), e);
            throw new RuntimeException("Failed to serialize payment message", e);
        } catch (Exception e) {
            log.error("Failed to publish payment message to SQS: paymentId={}", messageDTO.getPaymentId(), e);
            throw new RuntimeException("Failed to publish payment message to SQS", e);
        }
    }
}
