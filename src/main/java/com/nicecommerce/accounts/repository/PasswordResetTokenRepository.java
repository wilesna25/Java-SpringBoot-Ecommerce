package com.nicecommerce.accounts.repository;

import com.nicecommerce.accounts.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Password Reset Token Repository
 * 
 * Spring Data JPA repository for PasswordResetToken entity.
 * 
 * @author NiceCommerce Team
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /**
     * Find token by token string
     * 
     * @param token Token string
     * @return Optional PasswordResetToken
     */
    Optional<PasswordResetToken> findByToken(String token);

    /**
     * Find valid (unused and not expired) token
     * 
     * @param token Token string
     * @param now Current timestamp
     * @return Optional PasswordResetToken
     */
    @Query("SELECT t FROM PasswordResetToken t WHERE t.token = :token " +
           "AND t.used = false AND t.expiresAt > :now")
    Optional<PasswordResetToken> findValidToken(@Param("token") String token, 
                                                @Param("now") LocalDateTime now);

    /**
     * Delete expired tokens
     * 
     * @param now Current timestamp
     */
    @Query("DELETE FROM PasswordResetToken t WHERE t.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);
}

