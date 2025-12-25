package com.nicecommerce.products.repository;

import com.nicecommerce.products.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Category Repository
 * 
 * Spring Data JPA repository for Category entity.
 * 
 * @author NiceCommerce Team
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find category by slug
     * 
     * @param slug Category slug
     * @return Optional Category
     */
    Optional<Category> findBySlug(String slug);

    /**
     * Check if category exists by slug
     * 
     * @param slug Category slug
     * @return true if category exists
     */
    boolean existsBySlug(String slug);
}

