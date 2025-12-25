package com.nicecommerce.products.controller;

import com.nicecommerce.products.dto.CreateProductRequest;
import com.nicecommerce.products.dto.ProductDTO;
import com.nicecommerce.products.dto.ProductListResponse;
import com.nicecommerce.products.dto.UpdateProductRequest;
import com.nicecommerce.products.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Product Controller
 * 
 * REST API endpoints for product operations.
 * Implements contract-first API design with OpenAPI documentation.
 * 
 * @author NiceCommerce Team
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Products", description = "Product catalog operations")
public class ProductController {

    private final ProductService productService;

    /**
     * Get paginated list of products
     * 
     * GET /api/v1/products?page=0&size=20&category=slug&search=term&isDrop=true
     */
    @GetMapping
    @Operation(
        summary = "List products",
        description = "Retrieve a paginated list of products with optional filtering, searching, and sorting. " +
                     "Supports filtering by category, searching by name/description, and filtering drop products. " +
                     "Results are paginated and can be sorted by various fields.",
        tags = {"Products"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successful response",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProductListResponse.class),
                examples = @ExampleObject(
                    name = "Success Example",
                    value = "{\n" +
                           "  \"products\": [\n" +
                           "    {\n" +
                           "      \"id\": 1,\n" +
                           "      \"name\": \"Premium T-Shirt\",\n" +
                           "      \"slug\": \"premium-t-shirt\",\n" +
                           "      \"price\": 29.99,\n" +
                           "      \"categoryName\": \"Clothing\",\n" +
                           "      \"isActive\": true\n" +
                           "    }\n" +
                           "  ],\n" +
                           "  \"currentPage\": 0,\n" +
                           "  \"totalPages\": 5,\n" +
                           "  \"totalItems\": 100,\n" +
                           "  \"pageSize\": 20\n" +
                           "}"
                )
            )
        ),
        @ApiResponse(responseCode = "400", description = "Bad request - Invalid parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ProductListResponse> getProducts(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Filter by category slug") @RequestParam(required = false) String category,
            @Parameter(description = "Search term") @RequestParam(required = false) String search,
            @Parameter(description = "Filter drop products") @RequestParam(required = false) Boolean isDrop,
            @Parameter(description = "Sort field and direction") @RequestParam(defaultValue = "-createdAt") String sort) {

        Pageable pageable = PageRequest.of(page, size, 
                Sort.by(sort.startsWith("-") ? 
                        Sort.Direction.DESC : Sort.Direction.ASC,
                        sort.replace("-", "")));

        ProductListResponse response;
        
        if (search != null && !search.isBlank()) {
            response = productService.searchProducts(search, pageable);
        } else {
            response = productService.findAll(pageable);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Get product by ID
     * 
     * GET /api/v1/products/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieve a single product by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product found"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ProductDTO> getProductById(
            @Parameter(description = "Product ID") @PathVariable Long id) {
        ProductDTO product = productService.findById(id);
        return ResponseEntity.ok(product);
    }

    /**
     * Get product by slug
     * 
     * GET /api/v1/products/slug/{slug}
     */
    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get product by slug", description = "Retrieve a single product by slug")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product found"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ProductDTO> getProductBySlug(
            @Parameter(description = "Product slug") @PathVariable String slug) {
        // This would require a findBySlug method in service
        // For now, returning 404
        return ResponseEntity.notFound().build();
    }

    /**
     * Create a new product (Admin only)
     * 
     * POST /api/v1/products
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Create product",
        description = "Create a new product. Requires ADMIN role. " +
                     "The product slug will be automatically generated from the name. " +
                     "Category must exist before creating a product.",
        tags = {"Products"},
        security = @SecurityRequirement(name = "FirebaseAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Product created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProductDTO.class),
                examples = @ExampleObject(
                    name = "Created Product",
                    value = "{\n" +
                           "  \"id\": 1,\n" +
                           "  \"name\": \"New Product\",\n" +
                           "  \"slug\": \"new-product\",\n" +
                           "  \"price\": 39.99,\n" +
                           "  \"categoryName\": \"Clothing\",\n" +
                           "  \"isActive\": true\n" +
                           "}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - Validation failed",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Validation Error",
                    value = "{\n" +
                           "  \"timestamp\": \"2024-01-15T10:30:00Z\",\n" +
                           "  \"status\": 400,\n" +
                           "  \"error\": \"Bad Request\",\n" +
                           "  \"message\": \"Validation failed\",\n" +
                           "  \"errors\": {\n" +
                           "    \"name\": \"Product name is required\",\n" +
                           "    \"price\": \"Price must be positive\"\n" +
                           "  }\n" +
                           "}"
                )
            )
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid authentication token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions (ADMIN role required)")
    })
    public ResponseEntity<ProductDTO> createProduct(
            @Valid @RequestBody CreateProductRequest request) {
        ProductDTO product = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    /**
     * Update an existing product (Admin only)
     * 
     * PUT /api/v1/products/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Update product",
        description = "Update an existing product. Requires ADMIN role. " +
                     "Only provided fields will be updated. " +
                     "If the name is changed, the slug will be automatically regenerated.",
        tags = {"Products"},
        security = @SecurityRequirement(name = "FirebaseAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Product updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProductDTO.class)
            )
        ),
        @ApiResponse(responseCode = "400", description = "Bad request - Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid authentication token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions (ADMIN role required)"),
        @ApiResponse(
            responseCode = "404",
            description = "Product not found",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Not Found",
                    value = "{\n" +
                           "  \"timestamp\": \"2024-01-15T10:30:00Z\",\n" +
                           "  \"status\": 404,\n" +
                           "  \"error\": \"Not Found\",\n" +
                           "  \"message\": \"Product not found with id: 999\"\n" +
                           "}"
                )
            )
        )
    })
    public ResponseEntity<ProductDTO> updateProduct(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        ProductDTO product = productService.update(id, request);
        return ResponseEntity.ok(product);
    }

    /**
     * Delete a product (Admin only)
     * 
     * DELETE /api/v1/products/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Delete product",
        description = "Soft delete a product (sets isActive to false). Requires ADMIN role. " +
                     "The product is not physically deleted from the database, allowing for recovery if needed.",
        tags = {"Products"},
        security = @SecurityRequirement(name = "FirebaseAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Product deleted successfully (no content)"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid authentication token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions (ADMIN role required)"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID") @PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
