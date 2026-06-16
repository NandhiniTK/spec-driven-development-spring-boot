package com.nandhini.poc.paymentgateway.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebhookResponseDTO {
    
    private boolean received;
}
