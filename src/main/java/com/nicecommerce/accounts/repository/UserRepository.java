package com.nicecommerce.accounts.repository;

import com.nicecommerce.accounts.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User Repository
 * 
 * Spring Data JPA repository for User entity.
 * Provides CRUD operations and custom queries.
 * 
 * @author NiceCommerce Team
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email (used for authentication)
     * 
     * @param email User email address
     * @return Optional User
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if user exists by email
     * 
     * @param email User email address
     * @return true if user exists
     */
    boolean existsByEmail(String email);

    /**
     * Find active users by role
     * 
     * @param role User role
     * @return List of users
     */
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isActive = true")
    java.util.List<User> findActiveUsersByRole(@Param("role") User.UserRole role);
}

