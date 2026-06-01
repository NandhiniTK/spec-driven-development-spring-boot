package com.nandhini.poc.paymentgateway.service.handler;

import com.nandhini.poc.paymentgateway.entity.Payment;
import com.nandhini.poc.paymentgateway.entity.PaymentMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@Slf4j
public class UpiPaymentHandler implements PaymentMethodHandler {

    private final Random random = new Random();

    @Override
    public PaymentMethod getSupportedPaymentMethod() {
        return PaymentMethod.UPI;
    }

    @Override
    public boolean processPayment(Payment payment) {
        log.info("Processing UPI payment: paymentId={}, amount={}", 
                payment.getId(), payment.getAmount());
        
        // Mock implementation: 85% success rate
        boolean success = random.nextInt(100) < 85;
        
        if (success) {
            log.info("UPI payment successful: paymentId={}", payment.getId());
            payment.setGatewayTransactionId("upi_txn_" + System.currentTimeMillis());
        } else {
            log.warn("UPI payment failed: paymentId={}", payment.getId());
        }
        
        return success;
    }
}
