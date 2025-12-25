package com.nicecommerce.products.integration;

import com.nicecommerce.products.dto.CreateProductRequest;
import com.nicecommerce.products.dto.ProductDTO;
import com.nicecommerce.products.entity.Category;
import com.nicecommerce.products.entity.Product;
import com.nicecommerce.products.repository.CategoryRepository;
import com.nicecommerce.products.repository.ProductRepository;
import com.nicecommerce.products.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration Tests for ProductService
 * 
 * Tests with real database (H2 in-memory for tests).
 * 
 * @author NiceCommerce Team
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("ProductService Integration Tests")
class ProductIntegrationTest {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    private Category testCategory;
    
    @BeforeEach
    void setUp() {
        // Clean up
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        
        // Create test category
        testCategory = Category.builder()
            .name("Test Category")
            .slug("test-category")
            .build();
        testCategory = categoryRepository.save(testCategory);
    }
    
    @Test
    @DisplayName("Should create and retrieve product")
    void shouldCreateAndRetrieveProduct() {
        // Given
        CreateProductRequest request = CreateProductRequest.builder()
            .name("Integration Test Product")
            .description("Test Description")
            .price(new BigDecimal("29.99"))
            .categoryId(testCategory.getId())
            .build();
        
        // When
        ProductDTO created = productService.create(request);
        
        // Then
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Integration Test Product");
        assertThat(created.getPrice()).isEqualByComparingTo(new BigDecimal("29.99"));
        
        // Verify in database
        Product found = productRepository.findById(created.getId()).orElseThrow();
        assertThat(found.getName()).isEqualTo("Integration Test Product");
        assertThat(found.getSlug()).isEqualTo("integration-test-product");
    }
    
    @Test
    @DisplayName("Should update product")
    void shouldUpdateProduct() {
        // Given
        Product product = Product.builder()
            .name("Original Product")
            .slug("original-product")
            .description("Original Description")
            .price(new BigDecimal("19.99"))
            .category(testCategory)
            .isActive(true)
            .build();
        product = productRepository.save(product);
        
        com.nicecommerce.products.dto.UpdateProductRequest updateRequest = 
            com.nicecommerce.products.dto.UpdateProductRequest.builder()
                .name("Updated Product")
                .price(new BigDecimal("39.99"))
                .build();
        
        // When
        ProductDTO updated = productService.update(product.getId(), updateRequest);
        
        // Then
        assertThat(updated.getName()).isEqualTo("Updated Product");
        assertThat(updated.getPrice()).isEqualByComparingTo(new BigDecimal("39.99"));
        
        // Verify in database
        Product found = productRepository.findById(product.getId()).orElseThrow();
        assertThat(found.getName()).isEqualTo("Updated Product");
    }
    
    @Test
    @DisplayName("Should soft delete product")
    void shouldSoftDeleteProduct() {
        // Given
        Product product = Product.builder()
            .name("Product to Delete")
            .slug("product-to-delete")
            .price(new BigDecimal("19.99"))
            .category(testCategory)
            .isActive(true)
            .build();
        product = productRepository.save(product);
        
        // When
        productService.delete(product.getId());
        
        // Then
        Product found = productRepository.findById(product.getId()).orElseThrow();
        assertThat(found.getIsActive()).isFalse();
    }
}

