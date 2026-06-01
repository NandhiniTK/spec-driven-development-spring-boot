package com.nandhini.poc.paymentgateway.dto;

import com.nandhini.poc.paymentgateway.entity.Currency;
import com.nandhini.poc.paymentgateway.entity.PaymentMethod;
import com.nandhini.poc.paymentgateway.entity.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Payment response details")
public class PaymentResponseDTO {

    @Schema(description = "Payment unique identifier", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;
    
    @Schema(description = "User ID who initiated the payment", example = "user-123")
    private String userId;
    
    @Schema(description = "Payment amount", example = "10000")
    private BigDecimal amount;
    
    @Schema(description = "Currency code", example = "INR")
    private Currency currency;
    
    @Schema(description = "Payment method", example = "CARD")
    private PaymentMethod paymentMethod;
    
    @Schema(description = "Payment status", example = "PENDING")
    private PaymentStatus status;
    
    @Schema(description = "Gateway transaction ID from external payment provider", example = "ch_3NqZ8KLkdIwHu7ix0B3n0W8Z")
    private String gatewayTransactionId;
    
    @Schema(description = "Custom metadata")
    private Map<String, String> metadata;
    
    @Schema(description = "Payment creation timestamp", example = "2026-06-01T14:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "Payment last update timestamp", example = "2026-06-01T14:30:15")
    private LocalDateTime updatedAt;
}
