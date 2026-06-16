package com.nandhini.poc.paymentgateway.service.handler;

import com.nandhini.poc.paymentgateway.entity.Payment;
import com.nandhini.poc.paymentgateway.entity.PaymentMethod;

public interface PaymentMethodHandler {
    
    PaymentMethod getSupportedPaymentMethod();
    
    boolean processPayment(Payment payment);
}
