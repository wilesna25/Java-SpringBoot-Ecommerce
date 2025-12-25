package com.nicecommerce.products.service;

import com.nicecommerce.core.exception.ResourceNotFoundException;
import com.nicecommerce.products.dto.CreateProductRequest;
import com.nicecommerce.products.dto.ProductDTO;
import com.nicecommerce.products.dto.UpdateProductRequest;
import com.nicecommerce.products.entity.Category;
import com.nicecommerce.products.entity.Product;
import com.nicecommerce.products.mapper.ProductMapper;
import com.nicecommerce.products.repository.CategoryRepository;
import com.nicecommerce.products.repository.ProductRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit Tests for ProductService
 * 
 * Tests business logic in isolation using mocks.
 * 
 * @author NiceCommerce Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Unit Tests")
class ProductServiceTest {
    
    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private CategoryRepository categoryRepository;
    
    @Mock
    private ProductMapper productMapper;
    
    @Mock
    private MeterRegistry meterRegistry;
    
    @InjectMocks
    private ProductService productService;
    
    private Product testProduct;
    private Category testCategory;
    private ProductDTO testProductDTO;
    
    @BeforeEach
    void setUp() {
        testCategory = Category.builder()
            .id(1L)
            .name("Clothing")
            .slug("clothing")
            .build();
        
        testProduct = Product.builder()
            .id(1L)
            .name("Test Product")
            .slug("test-product")
            .description("Test Description")
            .price(new BigDecimal("29.99"))
            .category(testCategory)
            .isActive(true)
            .build();
        
        testProductDTO = ProductDTO.builder()
            .id(1L)
            .name("Test Product")
            .slug("test-product")
            .description("Test Description")
            .price(new BigDecimal("29.99"))
            .categoryName("Clothing")
            .isActive(true)
            .build();
    }
    
    @Test
    @DisplayName("Should find product by ID successfully")
    void shouldFindProductById() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productMapper.toDTO(testProduct)).thenReturn(testProductDTO);
        when(meterRegistry.timer(anyString(), anyString(), anyString())).thenReturn(
            io.micrometer.core.instrument.Timer.builder("test").register(meterRegistry));
        
        // When
        ProductDTO result = productService.findById(1L);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Product");
        verify(productRepository).findById(1L);
        verify(productMapper).toDTO(testProduct);
    }
    
    @Test
    @DisplayName("Should throw exception when product not found")
    void shouldThrowExceptionWhenProductNotFound() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        when(meterRegistry.timer(anyString(), anyString(), anyString())).thenReturn(
            io.micrometer.core.instrument.Timer.builder("test").register(meterRegistry));
        
        // When/Then
        assertThatThrownBy(() -> productService.findById(1L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Product not found");
        
        verify(productRepository).findById(1L);
        verify(productMapper, never()).toDTO(any());
    }
    
    @Test
    @DisplayName("Should create product successfully")
    void shouldCreateProduct() {
        // Given
        CreateProductRequest request = CreateProductRequest.builder()
            .name("New Product")
            .description("New Description")
            .price(new BigDecimal("39.99"))
            .categoryId(1L)
            .build();
        
        Product savedProduct = Product.builder()
            .id(2L)
            .name("New Product")
            .slug("new-product")
            .description("New Description")
            .price(new BigDecimal("39.99"))
            .category(testCategory)
            .build();
        
        ProductDTO savedProductDTO = ProductDTO.builder()
            .id(2L)
            .name("New Product")
            .slug("new-product")
            .price(new BigDecimal("39.99"))
            .categoryName("Clothing")
            .build();
        
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(productMapper.toEntity(request)).thenReturn(Product.builder()
            .name("New Product")
            .price(new BigDecimal("39.99"))
            .build());
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        when(productMapper.toDTO(savedProduct)).thenReturn(savedProductDTO);
        
        // When
        ProductDTO result = productService.create(request);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("New Product");
        verify(categoryRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
        verify(productMapper).toDTO(savedProduct);
    }
    
    @Test
    @DisplayName("Should update product successfully")
    void shouldUpdateProduct() {
        // Given
        UpdateProductRequest request = UpdateProductRequest.builder()
            .name("Updated Product")
            .price(new BigDecimal("49.99"))
            .build();
        
        Product updatedProduct = Product.builder()
            .id(1L)
            .name("Updated Product")
            .slug("updated-product")
            .price(new BigDecimal("49.99"))
            .category(testCategory)
            .build();
        
        ProductDTO updatedProductDTO = ProductDTO.builder()
            .id(1L)
            .name("Updated Product")
            .price(new BigDecimal("49.99"))
            .build();
        
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);
        when(productMapper.toDTO(updatedProduct)).thenReturn(updatedProductDTO);
        
        // When
        ProductDTO result = productService.update(1L, request);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Product");
        verify(productRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
        verify(productMapper).updateEntityFromDTO(request, testProduct);
    }
    
    @Test
    @DisplayName("Should delete product (soft delete)")
    void shouldDeleteProduct() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        // When
        productService.delete(1L);
        
        // Then
        verify(productRepository).findById(1L);
        verify(productRepository).save(testProduct);
        assertThat(testProduct.getIsActive()).isFalse();
    }
    
    @Test
    @DisplayName("Should find all products with pagination")
    void shouldFindAllProducts() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Product> productPage = new PageImpl<>(List.of(testProduct), pageable, 1);
        
        when(productRepository.findByIsActiveTrue(pageable)).thenReturn(productPage);
        when(productMapper.toDTOList(List.of(testProduct))).thenReturn(List.of(testProductDTO));
        when(meterRegistry.timer(anyString(), anyString(), anyString())).thenReturn(
            io.micrometer.core.instrument.Timer.builder("test").register(meterRegistry));
        
        // When
        var result = productService.findAll(pageable);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getProducts()).hasSize(1);
        assertThat(result.getTotalItems()).isEqualTo(1);
        verify(productRepository).findByIsActiveTrue(pageable);
    }
}

