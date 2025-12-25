package com.nicecommerce.products.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Update Product Request DTO
 * 
 * Request DTO for updating an existing product.
 * All fields are optional - only provided fields will be updated.
 * 
 * @author NiceCommerce Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to update a product")
public class UpdateProductRequest {
    
    @Schema(description = "Product name", example = "Premium T-Shirt")
    private String name;
    
    @Schema(description = "Product description")
    private String description;
    
    @Schema(description = "Product price", example = "29.99")
    @DecimalMin(value = "0.0", message = "Price must be positive")
    private BigDecimal price;
    
    @Schema(description = "Category ID", example = "1")
    private Long categoryId;
    
    @Schema(description = "Product images URLs")
    private List<String> images;
    
    @Schema(description = "Size to stock mapping")
    private Map<String, Integer> sizes;
    
    @Schema(description = "Product material")
    private String material;
    
    @Schema(description = "Size guide")
    private Map<String, Map<String, String>> sizeGuide;
    
    @Schema(description = "Occasions of use")
    private List<String> occasionsOfUse;
    
    @Schema(description = "Whether this is a drop product")
    private Boolean isDrop;
    
    @Schema(description = "Whether the product is active")
    private Boolean isActive;
    
    @Schema(description = "Release date for drop products")
    private java.time.LocalDateTime releaseDate;
}

