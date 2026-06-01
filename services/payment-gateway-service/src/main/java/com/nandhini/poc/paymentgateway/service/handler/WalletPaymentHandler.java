package com.nandhini.poc.paymentgateway.service.handler;

import com.nandhini.poc.paymentgateway.entity.Payment;
import com.nandhini.poc.paymentgateway.entity.PaymentMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@Slf4j
public class WalletPaymentHandler implements PaymentMethodHandler {

    private final Random random = new Random();

    @Override
    public PaymentMethod getSupportedPaymentMethod() {
        return PaymentMethod.WALLET;
    }

    @Override
    public boolean processPayment(Payment payment) {
        log.info("Processing WALLET payment: paymentId={}, amount={}", 
                payment.getId(), payment.getAmount());
        
        // Mock implementation: 90% success rate
        boolean success = random.nextInt(100) < 90;
        
        if (success) {
            log.info("WALLET payment successful: paymentId={}", payment.getId());
            payment.setGatewayTransactionId("wallet_txn_" + System.currentTimeMillis());
        } else {
            log.warn("WALLET payment failed: paymentId={}", payment.getId());
        }
        
        return success;
    }
}
