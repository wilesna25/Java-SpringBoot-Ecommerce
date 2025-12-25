package com.nicecommerce.products.controller;

import com.nicecommerce.core.exception.ResourceNotFoundException;
import com.nicecommerce.products.entity.Product;
import com.nicecommerce.products.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Product Controller
 * 
 * REST API endpoints for product operations.
 * 
 * @author NiceCommerce Team
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;

    /**
     * Get paginated list of products
     * 
     * GET /api/products?page=0&size=20&category=slug&search=term&isDrop=true
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isDrop,
            @RequestParam(defaultValue = "-createdAt") String sort) {

        Pageable pageable = PageRequest.of(page, size, 
                Sort.by(sort.startsWith("-") ? 
                        Sort.Direction.DESC : Sort.Direction.ASC,
                        sort.replace("-", "")));

        Page<Product> products;
        
        if (search != null && !search.isBlank()) {
            products = productRepository.searchProducts(search, pageable);
        } else if (isDrop != null && isDrop) {
            products = productRepository.findByIsDropTrueAndIsActiveTrue(pageable);
        } else {
            products = productRepository.findByIsActiveTrue(pageable);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("products", products.getContent());
        response.put("currentPage", products.getNumber());
        response.put("totalItems", products.getTotalElements());
        response.put("totalPages", products.getTotalPages());

        return ResponseEntity.ok(response);
    }

    /**
     * Get product by slug
     * 
     * GET /api/products/{slug}
     */
    @GetMapping("/{slug}")
    public ResponseEntity<Product> getProductBySlug(@PathVariable String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return ResponseEntity.ok(product);
    }
}

