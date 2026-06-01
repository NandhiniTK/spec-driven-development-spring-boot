package com.nandhini.poc.paymentgateway.controller;

import com.nandhini.poc.paymentgateway.dto.PaymentRequestDTO;
import com.nandhini.poc.paymentgateway.dto.PaymentResponseDTO;
import com.nandhini.poc.paymentgateway.entity.PaymentStatus;
import com.nandhini.poc.paymentgateway.exception.ErrorResponse;
import com.nandhini.poc.paymentgateway.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment", description = "Payment management APIs")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Initiate a new payment", description = "Creates a new payment request with idempotency support")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Payment initiated successfully",
                    content = @Content(schema = @Schema(implementation = PaymentResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or duplicate idempotency key with different payload",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable entity - business logic error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PaymentResponseDTO> initiatePayment(
            @Parameter(description = "Idempotency key to prevent duplicate payments", required = true)
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody PaymentRequestDTO requestDTO) {
        
        log.info("Received payment initiation request with idempotency key: {}", idempotencyKey);
        
        PaymentResponseDTO response = paymentService.initiatePayment(idempotencyKey, requestDTO);
        
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID", description = "Retrieves payment details by payment ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment found",
                    content = @Content(schema = @Schema(implementation = PaymentResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Payment not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PaymentResponseDTO> getPaymentById(
            @Parameter(description = "Payment ID", required = true)
            @PathVariable UUID id) {
        
        log.info("Fetching payment with ID: {}", id);
        
        PaymentResponseDTO response = paymentService.getPaymentById(id);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all payments", description = "Retrieves a paginated list of payments with optional status filter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payments retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<PaymentResponseDTO>> getAllPayments(
            @Parameter(description = "Filter by payment status")
            @RequestParam(required = false) PaymentStatus status,
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size (max 100)")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field and direction (e.g., createdAt,desc)")
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        
        log.info("Fetching payments - status: {}, page: {}, size: {}", status, page, size);
        
        // Limit page size to 100
        size = Math.min(size, 100);
        
        // Parse sort parameter
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction sortDirection = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortField));
        
        Page<PaymentResponseDTO> payments = paymentService.getAllPayments(status, pageable);
        
        return ResponseEntity.ok(payments);
    }
}
