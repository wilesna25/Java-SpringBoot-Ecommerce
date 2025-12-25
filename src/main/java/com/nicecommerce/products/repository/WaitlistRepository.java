package com.nicecommerce.products.repository;

import com.nicecommerce.products.entity.Waitlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Waitlist Repository
 * 
 * Spring Data JPA repository for Waitlist entity.
 * 
 * @author NiceCommerce Team
 */
@Repository
public interface WaitlistRepository extends JpaRepository<Waitlist, Long> {

    /**
     * Find waitlist entry by product, email, and size
     * 
     * @param productId Product ID
     * @param email Email address
     * @param size Size
     * @return Optional Waitlist
     */
    Optional<Waitlist> findByProductIdAndEmailAndSize(Long productId, String email, String size);

    /**
     * Find unnotified waitlist entries for a product
     * 
     * @param productId Product ID
     * @return List of Waitlist entries
     */
    @Query("SELECT w FROM Waitlist w WHERE w.product.id = :productId AND w.notified = false")
    List<Waitlist> findUnnotifiedByProductId(@Param("productId") Long productId);
}

