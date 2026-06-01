package com.nandhini.poc.paymentgateway.controller;

import com.nandhini.poc.paymentgateway.dto.WebhookRequestDTO;
import com.nandhini.poc.paymentgateway.dto.WebhookResponseDTO;
import com.nandhini.poc.paymentgateway.entity.Payment;
import com.nandhini.poc.paymentgateway.entity.PaymentTransaction;
import com.nandhini.poc.paymentgateway.exception.PaymentNotFoundException;
import com.nandhini.poc.paymentgateway.repository.PaymentRepository;
import com.nandhini.poc.paymentgateway.repository.PaymentTransactionRepository;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Webhook", description = "Payment gateway webhook APIs")
public class WebhookController {

    private final PaymentRepository paymentRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;

    @Value("${payment.gateway.webhook-secret}")
    private String webhookSecret;

    @PostMapping("/payment")
    @Transactional
    @Operation(summary = "Receive payment webhook", description = "Receives payment status updates from external payment gateway")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Webhook received successfully",
                    content = @Content(schema = @Schema(implementation = WebhookResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid signature or payload"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public ResponseEntity<WebhookResponseDTO> handlePaymentWebhook(
            @Parameter(description = "Webhook signature for verification", required = true)
            @RequestHeader("X-Webhook-Signature") String signature,
            @Valid @RequestBody WebhookRequestDTO webhookRequest) {
        
        log.info("Received payment webhook: event={}, paymentId={}", 
                webhookRequest.getEvent(), webhookRequest.getPaymentId());
        
        // Verify webhook signature
        if (!verifySignature(webhookRequest, signature)) {
            log.error("Invalid webhook signature for paymentId={}", webhookRequest.getPaymentId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        
        // Fetch payment
        Payment payment = paymentRepository.findById(webhookRequest.getPaymentId())
                .orElseThrow(() -> new PaymentNotFoundException(
                        "Payment not found: " + webhookRequest.getPaymentId()));
        
        // Update payment status
        payment.setStatus(webhookRequest.getStatus());
        if (webhookRequest.getGatewayTransactionId() != null) {
            payment.setGatewayTransactionId(webhookRequest.getGatewayTransactionId());
        }
        
        paymentRepository.save(payment);
        
        // Log webhook event
        logWebhookEvent(webhookRequest);
        
        log.info("Webhook processed successfully: paymentId={}, status={}", 
                payment.getId(), payment.getStatus());
        
        return ResponseEntity.ok(WebhookResponseDTO.builder()
                .received(true)
                .build());
    }

    private boolean verifySignature(WebhookRequestDTO request, String receivedSignature) {
        try {
            String payload = request.getEvent() + request.getPaymentId() + request.getStatus();
            
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String expectedSignature = HexFormat.of().formatHex(hash);
            
            return expectedSignature.equals(receivedSignature);
            
        } catch (Exception e) {
            log.error("Error verifying webhook signature", e);
            return false;
        }
    }

    private void logWebhookEvent(WebhookRequestDTO request) {
        try {
            PaymentTransaction transaction = PaymentTransaction.builder()
                    .paymentId(request.getPaymentId())
                    .eventType("WEBHOOK_RECEIVED")
                    .eventData(String.format("Event: %s, Status: %s, TxnId: %s", 
                            request.getEvent(), request.getStatus(), request.getGatewayTransactionId()))
                    .build();
            
            paymentTransactionRepository.save(transaction);
            
        } catch (Exception e) {
            log.error("Failed to log webhook event", e);
        }
    }
}
