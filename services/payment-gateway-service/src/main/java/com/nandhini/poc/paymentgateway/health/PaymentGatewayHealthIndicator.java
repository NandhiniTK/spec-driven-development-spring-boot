package com.nandhini.poc.paymentgateway.health;

import com.nandhini.poc.paymentgateway.gateway.PaymentGatewayClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentGatewayHealthIndicator implements HealthIndicator {

    private final PaymentGatewayClient paymentGatewayClient;

    @Override
    public Health health() {
        try {
            String gatewayName = paymentGatewayClient.getGatewayName();
            
            log.debug("Payment gateway health check: {}", gatewayName);
            
            return Health.up()
                    .withDetail("gateway", gatewayName)
                    .withDetail("status", "available")
                    .build();
                    
        } catch (Exception e) {
            log.error("Payment gateway health check failed", e);
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
