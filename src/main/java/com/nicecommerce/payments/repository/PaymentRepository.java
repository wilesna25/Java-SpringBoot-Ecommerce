package com.nicecommerce.payments.repository;

import com.nicecommerce.payments.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Payment Repository
 * 
 * Spring Data JPA repository for Payment entity.
 * 
 * @author NiceCommerce Team
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Find payment by payment ID from gateway
     * 
     * @param paymentId Payment ID
     * @return Optional Payment
     */
    Optional<Payment> findByPaymentId(String paymentId);
}

