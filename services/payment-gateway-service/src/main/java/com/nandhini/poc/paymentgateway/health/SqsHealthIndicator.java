package com.nandhini.poc.paymentgateway.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesRequest;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "spring.cloud.aws.sqs.enabled", havingValue = "true", matchIfMissing = false)
public class SqsHealthIndicator implements HealthIndicator {

    private final SqsClient sqsClient;

    @Value("${payment.queue.name}")
    private String queueName;

    @Override
    public Health health() {
        try {
            // Try to get queue attributes to verify connectivity
            GetQueueAttributesRequest request = GetQueueAttributesRequest.builder()
                    .queueUrl(queueName)
                    .attributeNames(QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES)
                    .build();
            
            var response = sqsClient.getQueueAttributes(request);
            int messageCount = Integer.parseInt(
                    response.attributes().getOrDefault(QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES, "0"));
            
            log.debug("SQS health check: queue={}, messages={}", queueName, messageCount);
            
            return Health.up()
                    .withDetail("queue", queueName)
                    .withDetail("approximateMessageCount", messageCount)
                    .withDetail("status", "connected")
                    .build();
                    
        } catch (Exception e) {
            log.warn("SQS health check failed: {}", e.getMessage());
            return Health.down()
                    .withDetail("queue", queueName)
                    .withDetail("error", e.getMessage())
                    .withDetail("message", "SQS not available - check AWS credentials and configuration")
                    .build();
        }
    }
}
