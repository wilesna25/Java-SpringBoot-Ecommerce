package com.nicecommerce.orders.repository;

import com.nicecommerce.accounts.entity.User;
import com.nicecommerce.orders.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Order Repository
 * 
 * Spring Data JPA repository for Order entity.
 * 
 * @author NiceCommerce Team
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Find order by order number
     * 
     * @param orderNumber Order number
     * @return Optional Order
     */
    Optional<Order> findByOrderNumber(String orderNumber);

    /**
     * Find orders by user with pagination
     * 
     * @param user User entity
     * @param pageable Pagination parameters
     * @return Page of Orders
     */
    Page<Order> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    /**
     * Find orders by status with pagination
     * 
     * @param status Order status
     * @param pageable Pagination parameters
     * @return Page of Orders
     */
    Page<Order> findByStatusOrderByCreatedAtDesc(Order.OrderStatus status, Pageable pageable);

    /**
     * Find orders by payment ID
     * 
     * @param paymentId Payment ID
     * @return Optional Order
     */
    Optional<Order> findByPaymentId(String paymentId);
}

