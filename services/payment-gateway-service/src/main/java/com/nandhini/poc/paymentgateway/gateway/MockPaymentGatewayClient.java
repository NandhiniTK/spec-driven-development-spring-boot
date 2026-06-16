package com.nandhini.poc.paymentgateway.gateway;

import com.nandhini.poc.paymentgateway.entity.Payment;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class MockPaymentGatewayClient implements PaymentGatewayClient {

    private final Random random = new Random();
    private final Timer gatewayApiTimer;

    @Override
    @CircuitBreaker(name = "paymentGateway", fallbackMethod = "processPaymentFallback")
    @Retry(name = "paymentGateway")
    @TimeLimiter(name = "paymentGateway")
    @Bulkhead(name = "paymentGateway")
    public GatewayResponse processPayment(Payment payment) {
        log.info("Processing payment through Mock Gateway: paymentId={}, amount={}, method={}", 
                payment.getId(), payment.getAmount(), payment.getPaymentMethod());
        
        // Start timer for gateway API latency
        Timer.Sample sample = Timer.start();
        
        try {
            // Simulate network latency
            Thread.sleep(random.nextInt(2000) + 500);
            
            // Simulate different success rates based on payment method
            int successThreshold = getSuccessThreshold(payment.getPaymentMethod());
            boolean success = random.nextInt(100) < successThreshold;
            
            GatewayResponse response;
            if (success) {
                String transactionId = generateMockTransactionId(payment.getPaymentMethod());
                log.info("Mock Gateway: Payment successful - paymentId={}, txnId={}", 
                        payment.getId(), transactionId);
                
                response = GatewayResponse.builder()
                        .success(true)
                        .transactionId(transactionId)
                        .message("Payment processed successfully")
                        .build();
            } else {
                log.warn("Mock Gateway: Payment failed - paymentId={}", payment.getId());
                
                response = GatewayResponse.builder()
                        .success(false)
                        .message("Payment declined by gateway")
                        .errorCode("PAYMENT_DECLINED")
                        .build();
            }
            
            // Record gateway API latency
            sample.stop(gatewayApiTimer);
            return response;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Mock Gateway: Payment processing interrupted", e);
            throw new RuntimeException("Payment processing interrupted", e);
        }
    }

    private GatewayResponse processPaymentFallback(Payment payment, Exception e) {
        log.error("Mock Gateway: Fallback triggered for paymentId={}, reason: {}", 
                payment.getId(), e.getMessage());
        
        return GatewayResponse.builder()
                .success(false)
                .message("Payment gateway timeout - please try again later")
                .errorCode("GATEWAY_TIMEOUT")
                .build();
    }

    @Override
    public String getGatewayName() {
        return "MockGateway";
    }

    private int getSuccessThreshold(com.nandhini.poc.paymentgateway.entity.PaymentMethod method) {
        return switch (method) {
            case CARD -> 80;
            case UPI -> 85;
            case WALLET -> 90;
            case NET_BANKING -> 75;
        };
    }

    private String generateMockTransactionId(com.nandhini.poc.paymentgateway.entity.PaymentMethod method) {
        String prefix = switch (method) {
            case CARD -> "card";
            case UPI -> "upi";
            case WALLET -> "wallet";
            case NET_BANKING -> "netbanking";
        };
        return prefix + "_txn_" + System.currentTimeMillis() + "_" + random.nextInt(10000);
    }
}
