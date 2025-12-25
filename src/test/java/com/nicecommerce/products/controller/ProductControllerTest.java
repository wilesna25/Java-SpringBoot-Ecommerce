package com.nicecommerce.products.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicecommerce.products.dto.CreateProductRequest;
import com.nicecommerce.products.dto.ProductDTO;
import com.nicecommerce.products.dto.ProductListResponse;
import com.nicecommerce.products.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller Tests for ProductController
 * 
 * Tests REST API endpoints with Spring MVC Test.
 * 
 * @author NiceCommerce Team
 */
@WebMvcTest(ProductController.class)
@DisplayName("ProductController Tests")
class ProductControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private ProductService productService;
    
    @Test
    @DisplayName("Should get products list successfully")
    void shouldGetProductsList() throws Exception {
        // Given
        ProductDTO productDTO = ProductDTO.builder()
            .id(1L)
            .name("Test Product")
            .price(new BigDecimal("29.99"))
            .build();
        
        ProductListResponse response = ProductListResponse.builder()
            .products(List.of(productDTO))
            .currentPage(0)
            .totalPages(1)
            .totalItems(1L)
            .pageSize(20)
            .build();
        
        when(productService.findAll(any())).thenReturn(response);
        
        // When/Then
        mockMvc.perform(get("/api/v1/products")
                .param("page", "0")
                .param("size", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.products").isArray())
            .andExpect(jsonPath("$.products[0].id").value(1))
            .andExpect(jsonPath("$.products[0].name").value("Test Product"))
            .andExpect(jsonPath("$.totalItems").value(1));
    }
    
    @Test
    @DisplayName("Should get product by ID successfully")
    void shouldGetProductById() throws Exception {
        // Given
        ProductDTO productDTO = ProductDTO.builder()
            .id(1L)
            .name("Test Product")
            .price(new BigDecimal("29.99"))
            .build();
        
        when(productService.findById(1L)).thenReturn(productDTO);
        
        // When/Then
        mockMvc.perform(get("/api/v1/products/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Test Product"));
    }
    
    @Test
    @DisplayName("Should create product successfully (Admin)")
    @WithMockUser(roles = "ADMIN")
    void shouldCreateProduct() throws Exception {
        // Given
        CreateProductRequest request = CreateProductRequest.builder()
            .name("New Product")
            .price(new BigDecimal("39.99"))
            .categoryId(1L)
            .build();
        
        ProductDTO productDTO = ProductDTO.builder()
            .id(2L)
            .name("New Product")
            .price(new BigDecimal("39.99"))
            .build();
        
        when(productService.create(any(CreateProductRequest.class))).thenReturn(productDTO);
        
        // When/Then
        mockMvc.perform(post("/api/v1/products")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.name").value("New Product"));
    }
    
    @Test
    @DisplayName("Should return 403 when non-admin tries to create product")
    @WithMockUser(roles = "USER")
    void shouldReturn403ForNonAdmin() throws Exception {
        // Given
        CreateProductRequest request = CreateProductRequest.builder()
            .name("New Product")
            .price(new BigDecimal("39.99"))
            .categoryId(1L)
            .build();
        
        // When/Then
        mockMvc.perform(post("/api/v1/products")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden());
    }
    
    @Test
    @DisplayName("Should update product successfully (Admin)")
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateProduct() throws Exception {
        // Given
        com.nicecommerce.products.dto.UpdateProductRequest request = 
            com.nicecommerce.products.dto.UpdateProductRequest.builder()
                .name("Updated Product")
                .price(new BigDecimal("49.99"))
                .build();
        
        ProductDTO productDTO = ProductDTO.builder()
            .id(1L)
            .name("Updated Product")
            .price(new BigDecimal("49.99"))
            .build();
        
        when(productService.update(eq(1L), any())).thenReturn(productDTO);
        
        // When/Then
        mockMvc.perform(put("/api/v1/products/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Updated Product"));
    }
    
    @Test
    @DisplayName("Should delete product successfully (Admin)")
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteProduct() throws Exception {
        // When/Then
        mockMvc.perform(delete("/api/v1/products/1")
                .with(csrf()))
            .andExpect(status().isNoContent());
    }
}

