package com.nandhini.poc.paymentgateway.repository;

import com.nandhini.poc.paymentgateway.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, UUID> {
    
    List<PaymentTransaction> findByPaymentIdOrderByTimestampDesc(UUID paymentId);
}
