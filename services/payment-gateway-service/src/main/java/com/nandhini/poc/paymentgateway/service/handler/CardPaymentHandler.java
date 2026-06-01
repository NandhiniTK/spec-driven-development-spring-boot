package com.nandhini.poc.paymentgateway.service.handler;

import com.nandhini.poc.paymentgateway.entity.Payment;
import com.nandhini.poc.paymentgateway.entity.PaymentMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@Slf4j
public class CardPaymentHandler implements PaymentMethodHandler {

    private final Random random = new Random();

    @Override
    public PaymentMethod getSupportedPaymentMethod() {
        return PaymentMethod.CARD;
    }

    @Override
    public boolean processPayment(Payment payment) {
        log.info("Processing CARD payment: paymentId={}, amount={}", 
                payment.getId(), payment.getAmount());
        
        // Mock implementation: 80% success rate
        boolean success = random.nextInt(100) < 80;
        
        if (success) {
            log.info("CARD payment successful: paymentId={}", payment.getId());
            payment.setGatewayTransactionId("card_txn_" + System.currentTimeMillis());
        } else {
            log.warn("CARD payment failed: paymentId={}", payment.getId());
        }
        
        return success;
    }
}
