package com.nandhini.poc.paymentgateway.dto;

import com.nandhini.poc.paymentgateway.entity.Currency;
import com.nandhini.poc.paymentgateway.entity.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Payment request details")
public class PaymentRequestDTO {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Amount must have at most 10 integer digits and 2 decimal places")
    @Schema(description = "Payment amount in smallest currency unit (paise for INR, cents for USD)", example = "10000", required = true)
    private BigDecimal amount;

    @NotNull(message = "Currency is required")
    @Schema(description = "Currency code (ISO 4217)", example = "INR", required = true)
    private Currency currency;

    @NotNull(message = "Payment method is required")
    @Schema(description = "Payment method", example = "CARD", required = true)
    private PaymentMethod paymentMethod;

    @Size(max = 20, message = "Metadata must not exceed 20 key-value pairs")
    @Schema(description = "Custom metadata (max 20 key-value pairs)", example = "{\"orderId\": \"ORD-12345\", \"customerId\": \"CUST-67890\"}")
    private Map<String, String> metadata;
}
