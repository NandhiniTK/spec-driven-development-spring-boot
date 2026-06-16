package com.nandhini.poc.paymentgateway.gateway;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GatewayResponse {
    
    private boolean success;
    private String transactionId;
    private String message;
    private String errorCode;
}
