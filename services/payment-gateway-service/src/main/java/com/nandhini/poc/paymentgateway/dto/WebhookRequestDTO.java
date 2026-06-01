package com.nandhini.poc.paymentgateway.dto;

import com.nandhini.poc.paymentgateway.entity.PaymentStatus;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebhookRequestDTO {
    
    private String event;
    private UUID paymentId;
    private String gatewayTransactionId;
    private PaymentStatus status;
    private String timestamp;
}
