package com.nicecommerce.products.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Product List Response DTO
 * 
 * Response DTO for paginated product lists.
 * 
 * @author NiceCommerce Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Paginated product list response")
public class ProductListResponse {
    
    @Schema(description = "List of products")
    private List<ProductDTO> products;
    
    @Schema(description = "Current page number (0-indexed)", example = "0")
    private Integer currentPage;
    
    @Schema(description = "Total number of pages", example = "5")
    private Integer totalPages;
    
    @Schema(description = "Total number of items", example = "100")
    private Long totalItems;
    
    @Schema(description = "Page size", example = "20")
    private Integer pageSize;
}

