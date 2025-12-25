package com.nicecommerce.products.repository;

import com.nicecommerce.products.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Product Repository
 * 
 * Spring Data JPA repository for Product entity.
 * 
 * @author NiceCommerce Team
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find product by slug
     * 
     * @param slug Product slug
     * @return Optional Product
     */
    Optional<Product> findBySlug(String slug);

    /**
     * Find active products with pagination
     * 
     * @param pageable Pagination parameters
     * @return Page of Products
     */
    Page<Product> findByIsActiveTrue(Pageable pageable);

    /**
     * Find active products by category with pagination
     * 
     * @param categoryId Category ID
     * @param pageable Pagination parameters
     * @return Page of Products
     */
    Page<Product> findByCategoryIdAndIsActiveTrue(Long categoryId, Pageable pageable);

    /**
     * Search products by name or description
     * 
     * @param searchTerm Search term
     * @param pageable Pagination parameters
     * @return Page of Products
     */
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Product> searchProducts(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find drop products
     * 
     * @param pageable Pagination parameters
     * @return Page of Products
     */
    Page<Product> findByIsDropTrueAndIsActiveTrue(Pageable pageable);
}

