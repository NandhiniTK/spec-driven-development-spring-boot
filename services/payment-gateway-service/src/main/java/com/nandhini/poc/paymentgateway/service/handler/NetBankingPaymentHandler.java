package com.nandhini.poc.paymentgateway.service.handler;

import com.nandhini.poc.paymentgateway.entity.Payment;
import com.nandhini.poc.paymentgateway.entity.PaymentMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@Slf4j
public class NetBankingPaymentHandler implements PaymentMethodHandler {

    private final Random random = new Random();

    @Override
    public PaymentMethod getSupportedPaymentMethod() {
        return PaymentMethod.NET_BANKING;
    }

    @Override
    public boolean processPayment(Payment payment) {
        log.info("Processing NET_BANKING payment: paymentId={}, amount={}", 
                payment.getId(), payment.getAmount());
        
        // Mock implementation: 75% success rate
        boolean success = random.nextInt(100) < 75;
        
        if (success) {
            log.info("NET_BANKING payment successful: paymentId={}", payment.getId());
            payment.setGatewayTransactionId("netbanking_txn_" + System.currentTimeMillis());
        } else {
            log.warn("NET_BANKING payment failed: paymentId={}", payment.getId());
        }
        
        return success;
    }
}
