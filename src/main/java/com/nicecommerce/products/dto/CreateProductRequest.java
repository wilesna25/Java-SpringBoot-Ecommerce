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
import java.util.List;
import java.util.Map;

/**
 * Create Product Request DTO
 * 
 * Request DTO for creating a new product.
 * 
 * @author NiceCommerce Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a new product")
public class CreateProductRequest {
    
    @Schema(description = "Product name", example = "Premium T-Shirt", required = true)
    @NotBlank(message = "Product name is required")
    private String name;
    
    @Schema(description = "Product description")
    private String description;
    
    @Schema(description = "Product price", example = "29.99", required = true)
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price must be positive")
    private BigDecimal price;
    
    @Schema(description = "Category ID", example = "1", required = true)
    @NotNull(message = "Category ID is required")
    private Long categoryId;
    
    @Schema(description = "Product images URLs")
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
    
    @Schema(description = "Release date for drop products")
    private java.time.LocalDateTime releaseDate;
}

