package com.nicecommerce.cart.repository;

import com.nicecommerce.accounts.entity.User;
import com.nicecommerce.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Cart Repository
 * 
 * Spring Data JPA repository for Cart entity.
 * 
 * @author NiceCommerce Team
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * Find cart by user
     * 
     * @param user User entity
     * @return Optional Cart
     */
    Optional<Cart> findByUser(User user);

    /**
     * Find cart by session key
     * 
     * @param sessionKey Session key
     * @return Optional Cart
     */
    Optional<Cart> findBySessionKey(String sessionKey);

    /**
     * Find cart by user or session key
     * 
     * @param user User entity (can be null)
     * @param sessionKey Session key (can be null)
     * @return Optional Cart
     */
    Optional<Cart> findByUserOrSessionKey(User user, String sessionKey);
}

