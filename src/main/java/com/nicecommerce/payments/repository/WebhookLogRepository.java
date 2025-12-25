package com.nicecommerce.payments.repository;

import com.nicecommerce.payments.entity.WebhookLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Webhook Log Repository
 * 
 * Spring Data JPA repository for WebhookLog entity.
 * 
 * @author NiceCommerce Team
 */
@Repository
public interface WebhookLogRepository extends JpaRepository<WebhookLog, Long> {
    // Basic CRUD operations provided by JpaRepository
}

