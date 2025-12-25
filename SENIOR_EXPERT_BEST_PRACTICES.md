# 🎯 Senior Expert Best Practices Guide
## Java/Spring Boot Best Practices with Code Samples

> **Author**: Senior Java/Spring Boot Expert (15+ years experience)  
> **Target Audience**: Senior developers, architects, tech leads  
> **Focus**: Production-ready, scalable, maintainable code

---

## 📋 Table of Contents

1. [Architecture Patterns](#architecture-patterns)
2. [Code Quality](#code-quality)
3. [Security Best Practices](#security-best-practices)
4. [Performance Optimization](#performance-optimization)
5. [Error Handling](#error-handling)
6. [Testing Best Practices](#testing-best-practices)
7. [API Design](#api-design)
8. [Database Best Practices](#database-best-practices)
9. [Caching Strategies](#caching-strategies)
10. [Observability & Monitoring](#observability--monitoring)

---

## 🏗️ Architecture Patterns

### Layered Architecture

```java
/**
 * BEST PRACTICE: Clear separation of concerns
 * 
 * Controller → Service → Repository → Database
 * 
 * Each layer has a single responsibility:
 * - Controller: HTTP concerns only
 * - Service: Business logic
 * - Repository: Data access
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) {
        // Controller is thin - delegates to service
        ProductDTO product = productService.findById(id);
        return ResponseEntity.ok(product);
    }
}

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    
    @Cacheable(value = "products", key = "#id")
    public ProductDTO findById(Long id) {
        // Business logic here
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return productMapper.toDTO(product);
    }
}
```

### Dependency Injection

```java
/**
 * BEST PRACTICE: Constructor injection (not field injection)
 * 
 * Benefits:
 * - Immutable dependencies
 * - Easier testing
 * - Clear dependencies
 */
@Service
@RequiredArgsConstructor  // Lombok generates constructor
public class ProductService {
    
    // Final fields = immutable dependencies
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final MeterRegistry meterRegistry;
    
    // No @Autowired needed with @RequiredArgsConstructor
}
```

---

## ✨ Code Quality

### Naming Conventions

```java
/**
 * BEST PRACTICE: Clear, descriptive names
 */
// ✅ Good
public class ProductService {
    public ProductDTO findById(Long productId) { }
    public ProductListResponse findAll(Pageable pageable) { }
    public ProductDTO create(CreateProductRequest request) { }
}

// ❌ Bad
public class ProdSvc {
    public ProductDTO get(Long id) { }
    public List<ProductDTO> list(Pageable p) { }
    public ProductDTO add(CreateProductRequest r) { }
}
```

### Method Design

```java
/**
 * BEST PRACTICE: Single Responsibility Principle
 * 
 * Each method should do ONE thing well
 */
@Service
public class OrderService {
    
    // ✅ Good: Single responsibility
    @Transactional
    public Order createOrder(User user, String idempotencyKey) {
        // Only creates order
        return orderRepository.save(buildOrder(user));
    }
    
    // ✅ Good: Separate method for idempotency check
    private boolean isIdempotentRequest(String idempotencyKey) {
        return redisTemplate.opsForValue()
            .get("idempotency:order:" + idempotencyKey) != null;
    }
    
    // ❌ Bad: Too many responsibilities
    public Order createOrderAndProcessPaymentAndSendEmail(
            User user, PaymentInfo payment, EmailTemplate template) {
        // Too many responsibilities!
    }
}
```

### Exception Handling

```java
/**
 * BEST PRACTICE: Custom exceptions with clear messages
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex) {
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Resource Not Found")
            .message(ex.getMessage())
            .path(getRequestPath())
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex) {
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Business Rule Violation")
            .message(ex.getMessage())
            .path(getRequestPath())
            .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
```

---

## 🔒 Security Best Practices

### Input Validation

```java
/**
 * BEST PRACTICE: Validate all inputs
 */
@RestController
public class ProductController {
    
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(
            @Valid @RequestBody CreateProductRequest request) {
        // @Valid ensures validation annotations are checked
        return ResponseEntity.ok(productService.create(request));
    }
}

@Data
public class CreateProductRequest {
    
    @NotBlank(message = "Product name is required")
    @Size(min = 3, max = 255, message = "Name must be between 3 and 255 characters")
    private String name;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
    private BigDecimal price;
    
    @NotNull(message = "Category ID is required")
    private Long categoryId;
}
```

### Authentication & Authorization

```java
/**
 * BEST PRACTICE: Use Spring Security with method-level security
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // For stateless APIs
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.decoder(jwtDecoder()))
            );
        return http.build();
    }
}

@Service
public class ProductService {
    
    @PreAuthorize("hasRole('ADMIN')")
    public ProductDTO create(CreateProductRequest request) {
        // Only admins can create products
    }
    
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ProductDTO update(Long id, UpdateProductRequest request, Long userId) {
        // Admins or resource owners can update
    }
}
```

---

## ⚡ Performance Optimization

### Caching Strategy

```java
/**
 * BEST PRACTICE: Multi-level caching
 */
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        // L1: Local cache (Caffeine)
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .recordStats());
        
        // L2: Distributed cache (Redis) - configured separately
        return cacheManager;
    }
}

@Service
public class ProductService {
    
    // Cache frequently accessed data
    @Cacheable(value = "products", key = "#id")
    public ProductDTO findById(Long id) {
        return productMapper.toDTO(productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found")));
    }
    
    // Evict cache on updates
    @CacheEvict(value = "products", key = "#id")
    public ProductDTO update(Long id, UpdateProductRequest request) {
        // Update logic
    }
    
    // Evict all cache entries
    @CacheEvict(value = "products", allEntries = true)
    public void clearCache() {
        // Clear all product cache
    }
}
```

### Database Query Optimization

```java
/**
 * BEST PRACTICE: Avoid N+1 queries
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // ✅ Good: Use JOIN FETCH to avoid N+1
    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.id = :id")
    Optional<Product> findByIdWithCategory(@Param("id") Long id);
    
    // ✅ Good: Use EntityGraph
    @EntityGraph(attributePaths = {"category"})
    Page<Product> findByIsActiveTrue(Pageable pageable);
    
    // ❌ Bad: Causes N+1 queries
    List<Product> findAll();  // Each product.category triggers separate query
}
```

### Pagination

```java
/**
 * BEST PRACTICE: Always use pagination for lists
 */
@RestController
public class ProductController {
    
    @GetMapping
    public ResponseEntity<ProductListResponse> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        ProductListResponse response = productService.findAll(pageable);
        return ResponseEntity.ok(response);
    }
}
```

---

## 🛡️ Error Handling

### Structured Error Responses

```java
/**
 * BEST PRACTICE: Consistent error response format
 */
@Data
@Builder
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private List<ValidationError> validationErrors;
    
    @Data
    @Builder
    public static class ValidationError {
        private String field;
        private String message;
        private Object rejectedValue;
    }
}

@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {
        
        List<ErrorResponse.ValidationError> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> ErrorResponse.ValidationError.builder()
                .field(error.getField())
                .message(error.getDefaultMessage())
                .rejectedValue(error.getRejectedValue())
                .build())
            .collect(Collectors.toList());
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Validation Failed")
            .message("Invalid input parameters")
            .path(getRequestPath())
            .validationErrors(errors)
            .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
```

---

## 🧪 Testing Best Practices

### Test Structure

```java
/**
 * BEST PRACTICE: AAA Pattern (Arrange-Act-Assert)
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    
    @Mock
    private ProductRepository productRepository;
    
    @InjectMocks
    private ProductService productService;
    
    @Test
    @DisplayName("Should find product by ID successfully")
    void findById_WhenProductExists_ReturnsProduct() {
        // Arrange
        Long productId = 1L;
        Product product = Product.builder()
            .id(productId)
            .name("Test Product")
            .build();
        when(productRepository.findById(productId))
            .thenReturn(Optional.of(product));
        
        // Act
        ProductDTO result = productService.findById(productId);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(productId);
        assertThat(result.getName()).isEqualTo("Test Product");
        verify(productRepository).findById(productId);
    }
}
```

### Test Data Builders

```java
/**
 * BEST PRACTICE: Use builders for test data
 */
public class ProductTestBuilder {
    
    public static Product.ProductBuilder defaultProduct() {
        return Product.builder()
            .name("Test Product")
            .slug("test-product")
            .description("Test Description")
            .price(new BigDecimal("29.99"))
            .isActive(true);
    }
    
    public static Product.ProductBuilder withCategory(Category category) {
        return defaultProduct().category(category);
    }
    
    public static Product.ProductBuilder inactive() {
        return defaultProduct().isActive(false);
    }
}

// Usage in tests
@Test
void testSomething() {
    Product product = ProductTestBuilder
        .defaultProduct()
        .id(1L)
        .build();
}
```

---

## 🌐 API Design

### RESTful API Design

```java
/**
 * BEST PRACTICE: Follow REST conventions
 */
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    
    // GET /api/v1/products - List all
    @GetMapping
    public ResponseEntity<ProductListResponse> getAllProducts(Pageable pageable) {
        return ResponseEntity.ok(productService.findAll(pageable));
    }
    
    // GET /api/v1/products/{id} - Get one
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }
    
    // POST /api/v1/products - Create
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(
            @Valid @RequestBody CreateProductRequest request) {
        ProductDTO product = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .header("Location", "/api/v1/products/" + product.getId())
            .body(product);
    }
    
    // PUT /api/v1/products/{id} - Update
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.update(id, request));
    }
    
    // DELETE /api/v1/products/{id} - Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

### API Versioning

```java
/**
 * BEST PRACTICE: Version your APIs
 */
@RestController
@RequestMapping("/api/v1/products")
public class ProductControllerV1 {
    // V1 implementation
}

@RestController
@RequestMapping("/api/v2/products")
public class ProductControllerV2 {
    // V2 implementation with breaking changes
}
```

---

## 🗄️ Database Best Practices

### Transaction Management

```java
/**
 * BEST PRACTICE: Use @Transactional appropriately
 */
@Service
@RequiredArgsConstructor
public class OrderService {
    
    // ✅ Good: Read-only transaction for queries
    @Transactional(readOnly = true)
    public Order findById(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }
    
    // ✅ Good: Write transaction for modifications
    @Transactional
    public Order createOrder(User user, String idempotencyKey) {
        // Multiple database operations in one transaction
        Order order = buildOrder(user);
        order = orderRepository.save(order);
        storeIdempotencyKey(idempotencyKey, order.getId());
        return order;
    }
    
    // ❌ Bad: Don't use @Transactional on controller methods
}
```

### Entity Design

```java
/**
 * BEST PRACTICE: Proper entity relationships
 */
@Entity
@Table(name = "products")
public class Product extends BaseEntity {
    
    // ✅ Good: Lazy loading for relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    // ✅ Good: Indexes for frequently queried fields
    @Index(name = "idx_product_name", columnList = "name")
    @Index(name = "idx_product_category", columnList = "category_id")
    // ... (defined at class level)
    
    // ✅ Good: Validation annotations
    @NotBlank
    @Size(max = 255)
    private String name;
    
    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal price;
}
```

---

## 📊 Observability & Monitoring

### Structured Logging

```java
/**
 * BEST PRACTICE: Structured logging with context
 */
@Service
@Slf4j
public class ProductService {
    
    public ProductDTO findById(Long id) {
        log.info("Fetching product - productId: {}, operation: findById", id);
        
        try {
            Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            
            log.info("Product fetched successfully - productId: {}, productName: {}", 
                id, product.getName());
            
            return productMapper.toDTO(product);
        } catch (Exception e) {
            log.error("Error fetching product - productId: {}, error: {}", 
                id, e.getMessage(), e);
            throw e;
        }
    }
}
```

### Metrics

```java
/**
 * BEST PRACTICE: Track business metrics
 */
@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final MeterRegistry meterRegistry;
    
    public ProductDTO findById(Long id) {
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            // Business logic
            return productMapper.toDTO(productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found")));
        } finally {
            sample.stop(Timer.builder("product.fetch.duration")
                .tag("operation", "findById")
                .register(meterRegistry));
        }
    }
}
```

---

## 📚 Additional Resources

- [Spring Boot Best Practices](https://docs.spring.io/spring-boot/docs/current/reference/html/best-practices.html)
- [Java Code Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-contents.html)
- [Effective Java by Joshua Bloch](https://www.oreilly.com/library/view/effective-java/9780134686097/)

---

**Last Updated**: 2024  
**Status**: ✅ Production Ready  
**Maintained By**: Senior Java/Spring Boot Expert Team

