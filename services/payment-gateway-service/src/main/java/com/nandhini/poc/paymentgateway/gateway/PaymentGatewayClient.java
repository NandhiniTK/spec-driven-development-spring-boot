package com.nandhini.poc.paymentgateway.gateway;

import com.nandhini.poc.paymentgateway.entity.Payment;

public interface PaymentGatewayClient {
    
    /**
     * Processes payment through external gateway.
     * 
     * @param payment Payment to process
     * @return GatewayResponse with transaction ID and status
     */
    GatewayResponse processPayment(Payment payment);
    
    /**
     * Gets the name of the gateway implementation.
     * 
     * @return Gateway name
     */
    String getGatewayName();
}
