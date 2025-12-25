package com.nicecommerce.products.entity;

import com.nicecommerce.core.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Product Entity
 * 
 * Represents a product in the e-commerce system.
 * 
 * This is equivalent to Django's products.Product model.
 * 
 * @author NiceCommerce Team
 */
@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_product_name", columnList = "name"),
    @Index(name = "idx_product_category", columnList = "category_id"),
    @Index(name = "idx_product_price", columnList = "price"),
    @Index(name = "idx_product_drop", columnList = "is_drop"),
    @Index(name = "idx_product_active", columnList = "is_active"),
    @Index(name = "idx_product_release", columnList = "release_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    /**
     * Product name
     */
    @NotBlank
    @Column(nullable = false, length = 255)
    private String name;

    /**
     * URL-friendly slug (unique)
     */
    @NotBlank
    @Column(unique = true, nullable = false, length = 255)
    private String slug;

    /**
     * Product description
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    /**
     * Product price
     */
    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * Product category
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /**
     * List of image URLs (stored as JSON)
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSON")
    @Builder.Default
    private List<String> images = List.of();

    /**
     * Size to stock mapping (stored as JSON)
     * Example: {"S": 10, "M": 5, "L": 8}
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSON")
    @Builder.Default
    private Map<String, Integer> sizes = new HashMap<>();

    /**
     * Product material
     */
    @Column(length = 255)
    private String material;

    /**
     * Size guide (stored as JSON)
     * Example: {"S": {"chest": "36", "length": "28"}, ...}
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "size_guide", columnDefinition = "JSON")
    @Builder.Default
    private Map<String, Map<String, String>> sizeGuide = new HashMap<>();

    /**
     * Occasions of use (stored as JSON array)
     * Example: ["casual", "formal"]
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "occasions_of_use", columnDefinition = "JSON")
    @Builder.Default
    private List<String> occasionsOfUse = List.of();

    /**
     * Whether this is a drop product
     */
    @Column(name = "is_drop", nullable = false)
    @Builder.Default
    private Boolean isDrop = false;

    /**
     * Release date for drop products
     */
    @Column(name = "release_date")
    private LocalDateTime releaseDate;

    /**
     * Whether the product is active/available
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Check if product has any stock
     */
    public boolean isAvailable() {
        if (sizes == null || sizes.isEmpty()) {
            return false;
        }
        return sizes.values().stream()
                .anyMatch(stock -> stock != null && stock > 0);
    }

    /**
     * Get total stock across all sizes
     */
    public int getTotalStock() {
        if (sizes == null || sizes.isEmpty()) {
            return 0;
        }
        return sizes.values().stream()
                .filter(stock -> stock != null)
                .mapToInt(Integer::intValue)
                .sum();
    }

    /**
     * Get stock for a specific size
     */
    public int getStockForSize(String size) {
        if (sizes == null) {
            return 0;
        }
        return sizes.getOrDefault(size, 0);
    }

    /**
     * Decrease stock for a size
     */
    public boolean decreaseStock(String size, int quantity) {
        int currentStock = getStockForSize(size);
        if (currentStock >= quantity) {
            sizes.put(size, currentStock - quantity);
            return true;
        }
        return false;
    }

    /**
     * Increase stock for a size
     */
    public void increaseStock(String size, int quantity) {
        int currentStock = getStockForSize(size);
        sizes.put(size, currentStock + quantity);
    }
}

