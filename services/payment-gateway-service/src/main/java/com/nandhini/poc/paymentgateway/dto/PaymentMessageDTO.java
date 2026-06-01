package com.nandhini.poc.paymentgateway.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMessageDTO {
    
    private UUID paymentId;
}
