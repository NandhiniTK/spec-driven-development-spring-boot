package com.nandhini.poc.paymentgateway.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public Counter paymentSuccessCounter(MeterRegistry registry) {
        return Counter.builder("payment.gateway.success")
                .description("Total number of successful payments")
                .tag("service", "payment-gateway")
                .register(registry);
    }

    @Bean
    public Counter paymentFailureCounter(MeterRegistry registry) {
        return Counter.builder("payment.gateway.failure")
                .description("Total number of failed payments")
                .tag("service", "payment-gateway")
                .register(registry);
    }

    @Bean
    public Counter paymentTimeoutCounter(MeterRegistry registry) {
        return Counter.builder("payment.gateway.timeout")
                .description("Total number of payment timeouts")
                .tag("service", "payment-gateway")
                .register(registry);
    }

    @Bean
    public Timer paymentProcessingTimer(MeterRegistry registry) {
        return Timer.builder("payment.gateway.processing.time")
                .description("Payment processing time")
                .tag("service", "payment-gateway")
                .register(registry);
    }

    @Bean
    public Timer gatewayApiTimer(MeterRegistry registry) {
        return Timer.builder("payment.gateway.api.latency")
                .description("Payment gateway API call latency")
                .tag("service", "payment-gateway")
                .register(registry);
    }
}
