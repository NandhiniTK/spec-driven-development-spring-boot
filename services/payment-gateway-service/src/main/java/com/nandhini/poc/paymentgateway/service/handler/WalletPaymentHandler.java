package com.nandhini.poc.paymentgateway.service.handler;

import com.nandhini.poc.paymentgateway.entity.Payment;
import com.nandhini.poc.paymentgateway.entity.PaymentMethod;
import com.nandhini.poc.paymentgateway.gateway.GatewayResponse;
import com.nandhini.poc.paymentgateway.gateway.PaymentGatewayClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WalletPaymentHandler implements PaymentMethodHandler {

    private final PaymentGatewayClient paymentGatewayClient;

    @Override
    public PaymentMethod getSupportedPaymentMethod() {
        return PaymentMethod.WALLET;
    }

    @Override
    public boolean processPayment(Payment payment) {
        log.info("Processing WALLET payment via gateway: paymentId={}, amount={}", 
                payment.getId(), payment.getAmount());
        
        GatewayResponse response = paymentGatewayClient.processPayment(payment);
        
        if (response.isSuccess()) {
            log.info("WALLET payment successful: paymentId={}, txnId={}", 
                    payment.getId(), response.getTransactionId());
            payment.setGatewayTransactionId(response.getTransactionId());
            return true;
        } else {
            log.warn("WALLET payment failed: paymentId={}, error={}", 
                    payment.getId(), response.getMessage());
            return false;
        }
    }
}
