package com.nandhini.poc.paymentgateway.repository;

import com.nandhini.poc.paymentgateway.entity.Payment;
import com.nandhini.poc.paymentgateway.entity.PaymentStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    
    /**
     * Fetches payment with pessimistic write lock (SELECT FOR UPDATE).
     * Used by payment processor to prevent concurrent processing of the same payment.
     * 
     * @param id Payment ID
     * @return Payment with exclusive lock
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Payment p WHERE p.id = :id")
    Optional<Payment> findByIdWithLock(@Param("id") UUID id);
    
    Page<Payment> findByUserId(String userId, Pageable pageable);
    
    Page<Payment> findByUserIdAndStatus(String userId, PaymentStatus status, Pageable pageable);
}
