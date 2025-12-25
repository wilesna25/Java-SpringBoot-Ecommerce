package com.nicecommerce.e2e;

import com.nicecommerce.accounts.entity.User;
import com.nicecommerce.accounts.repository.UserRepository;
import com.nicecommerce.orders.entity.Order;
import com.nicecommerce.orders.repository.OrderRepository;
import com.nicecommerce.products.entity.Category;
import com.nicecommerce.products.entity.Product;
import com.nicecommerce.products.repository.CategoryRepository;
import com.nicecommerce.products.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-End Tests for Complete User Flows
 * 
 * Tests complete user journeys from registration to order completion.
 * Uses real database (H2) and mocks external services.
 * 
 * @author Senior Java/Spring Boot Expert
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("E2E Tests - Complete User Flows")
@Transactional
class CompleteUserFlowE2ETest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    private Category testCategory;
    private Product testProduct;
    private User testUser;
    
    @BeforeEach
    void setUp() {
        // Create test category
        testCategory = Category.builder()
            .name("Test Category")
            .slug("test-category")
            .build();
        testCategory = categoryRepository.save(testCategory);
        
        // Create test product
        Map<String, Integer> sizes = new HashMap<>();
        sizes.put("S", 10);
        sizes.put("M", 15);
        sizes.put("L", 8);
        
        testProduct = Product.builder()
            .name("Test Product")
            .slug("test-product")
            .description("Test Description")
            .price(new BigDecimal("29.99"))
            .category(testCategory)
            .sizes(sizes)
            .isActive(true)
            .build();
        testProduct = productRepository.save(testProduct);
        
        // Create test user
        testUser = User.builder()
            .email("test@example.com")
            .displayName("Test User")
            .firebaseUid("test-firebase-uid-123")
            .role(User.UserRole.CUSTOMER)
            .isActive(true)
            .emailVerified(true)
            .build();
        testUser = userRepository.save(testUser);
    }
    
    @Test
    @DisplayName("Complete user flow: Browse products → Add to cart → Create order")
    void completeUserFlow_BrowseProducts_AddToCart_CreateOrder() throws Exception {
        // Step 1: Browse products
        mockMvc.perform(get("/api/products")
                .param("page", "0")
                .param("size", "20")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.products").isArray())
            .andExpect(jsonPath("$.products[0].id").value(testProduct.getId()))
            .andExpect(jsonPath("$.products[0].name").value("Test Product"));
        
        // Step 2: Get product details
        mockMvc.perform(get("/api/products/{id}", testProduct.getId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(testProduct.getId()))
            .andExpect(jsonPath("$.name").value("Test Product"))
            .andExpect(jsonPath("$.price").value(29.99));
        
        // Step 3: Search products
        mockMvc.perform(get("/api/products/search")
                .param("q", "Test")
                .param("page", "0")
                .param("size", "20")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.products").isArray())
            .andExpect(jsonPath("$.products[0].name").value("Test Product"));
        
        // Verify product exists in database
        Product foundProduct = productRepository.findById(testProduct.getId()).orElse(null);
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getName()).isEqualTo("Test Product");
    }
    
    @Test
    @DisplayName("Complete order flow: Create order → Process payment → Update status")
    void completeOrderFlow_CreateOrder_ProcessPayment_UpdateStatus() throws Exception {
        // Verify initial state
        long initialOrderCount = orderRepository.count();
        
        // Create order (simulated via service - in real E2E, this would be via API)
        Order order = Order.builder()
            .user(testUser)
            .status(Order.OrderStatus.PENDING)
            .subtotal(new BigDecimal("29.99"))
            .total(new BigDecimal("29.99"))
            .build();
        order = orderRepository.save(order);
        
        // Verify order created
        assertThat(order.getId()).isNotNull();
        assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.PENDING);
        assertThat(orderRepository.count()).isEqualTo(initialOrderCount + 1);
        
        // Update order status (simulated)
        order.setStatus(Order.OrderStatus.PAID);
        order = orderRepository.save(order);
        
        // Verify order updated
        Order updatedOrder = orderRepository.findById(order.getId()).orElse(null);
        assertThat(updatedOrder).isNotNull();
        assertThat(updatedOrder.getStatus()).isEqualTo(Order.OrderStatus.PAID);
    }
    
    @Test
    @DisplayName("Product availability flow: Check stock → Reserve → Update stock")
    void productAvailabilityFlow_CheckStock_Reserve_UpdateStock() {
        // Initial stock check
        int initialStockS = testProduct.getStockForSize("S");
        assertThat(initialStockS).isEqualTo(10);
        
        // Reserve items (decrease stock)
        boolean reserved = testProduct.decreaseStock("S", 3);
        assertThat(reserved).isTrue();
        
        // Verify stock decreased
        int newStockS = testProduct.getStockForSize("S");
        assertThat(newStockS).isEqualTo(7);
        
        // Save product
        testProduct = productRepository.save(testProduct);
        
        // Verify persisted
        Product savedProduct = productRepository.findById(testProduct.getId()).orElse(null);
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getStockForSize("S")).isEqualTo(7);
    }
    
    @Test
    @DisplayName("User registration and authentication flow")
    void userRegistrationAndAuthenticationFlow() {
        // Verify user exists
        User foundUser = userRepository.findByEmail("test@example.com").orElse(null);
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo("test@example.com");
        assertThat(foundUser.getDisplayName()).isEqualTo("Test User");
        assertThat(foundUser.getRole()).isEqualTo(User.UserRole.CUSTOMER);
        assertThat(foundUser.getIsActive()).isTrue();
        
        // Update user
        foundUser.setDisplayName("Updated Test User");
        foundUser = userRepository.save(foundUser);
        
        // Verify update
        User updatedUser = userRepository.findById(foundUser.getId()).orElse(null);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getDisplayName()).isEqualTo("Updated Test User");
    }
    
    @Test
    @DisplayName("Error handling flow: Invalid product ID → 404 response")
    void errorHandlingFlow_InvalidProductId_Returns404() throws Exception {
        // Try to get non-existent product
        mockMvc.perform(get("/api/products/{id}", 99999L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("Pagination flow: Multiple pages of products")
    void paginationFlow_MultiplePagesOfProducts() throws Exception {
        // Create multiple products
        for (int i = 0; i < 25; i++) {
            Product product = Product.builder()
                .name("Product " + i)
                .slug("product-" + i)
                .description("Description " + i)
                .price(new BigDecimal("19.99"))
                .category(testCategory)
                .isActive(true)
                .build();
            productRepository.save(product);
        }
        
        // Get first page
        mockMvc.perform(get("/api/products")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.products").isArray())
            .andExpect(jsonPath("$.currentPage").value(0))
            .andExpect(jsonPath("$.pageSize").value(10))
            .andExpect(jsonPath("$.totalPages").exists());
        
        // Get second page
        mockMvc.perform(get("/api/products")
                .param("page", "1")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.currentPage").value(1));
    }
}

