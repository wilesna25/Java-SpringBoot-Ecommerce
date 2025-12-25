package com.nicecommerce.products.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Product Data Transfer Object
 * 
 * DTO for transferring product data between layers.
 * This separates the API contract from the internal entity structure.
 * 
 * @author NiceCommerce Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Product information")
public class ProductDTO {
    
    @Schema(description = "Product ID", example = "1")
    private Long id;
    
    @Schema(description = "Product name", example = "Premium T-Shirt")
    @NotBlank(message = "Product name is required")
    private String name;
    
    @Schema(description = "Product slug", example = "premium-t-shirt")
    private String slug;
    
    @Schema(description = "Product description")
    private String description;
    
    @Schema(description = "Product price", example = "29.99")
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price must be positive")
    private BigDecimal price;
    
    @Schema(description = "Category name", example = "Clothing")
    private String categoryName;
    
    @Schema(description = "Category ID")
    private Long categoryId;
    
    @Schema(description = "Product images")
    private List<String> images;
    
    @Schema(description = "Size to stock mapping", example = "{\"S\": 10, \"M\": 15, \"L\": 8}")
    private Map<String, Integer> sizes;
    
    @Schema(description = "Product material")
    private String material;
    
    @Schema(description = "Size guide")
    private Map<String, Map<String, String>> sizeGuide;
    
    @Schema(description = "Occasions of use")
    private List<String> occasionsOfUse;
    
    @Schema(description = "Whether this is a drop product", example = "false")
    private Boolean isDrop;
    
    @Schema(description = "Whether the product is active", example = "true")
    private Boolean isActive;
    
    @Schema(description = "Release date for drop products")
    private LocalDateTime releaseDate;
    
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;
    
    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
}

