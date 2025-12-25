package com.nicecommerce.products.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Category Entity
 * 
 * Represents a product category.
 * 
 * This is equivalent to Django's products.Category model.
 * 
 * @author NiceCommerce Team
 */
@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Category name (unique)
     */
    @Column(unique = true, nullable = false, length = 100)
    private String name;

    /**
     * URL-friendly slug (unique)
     */
    @Column(unique = true, nullable = false, length = 100)
    private String slug;

    /**
     * Category description
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Creation timestamp
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Products in this category
     */
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Product> products = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}

