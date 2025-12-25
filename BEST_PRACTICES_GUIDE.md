# 🎯 Best Practices Guide - Senior Backend Engineer

> **Comprehensive best practices for Java/Spring Boot Mobile API development**

---

## 📋 Table of Contents

1. [Code Organization](#1-code-organization)
2. [API Design](#2-api-design)
3. [Data Access](#3-data-access)
4. [Security](#4-security)
5. [Performance](#5-performance)
6. [Testing](#6-testing)
7. [Observability](#7-observability)
8. [Resilience](#8-resilience)
9. [Documentation](#9-documentation)

---

## 1. Code Organization

### 1.1 Layered Architecture

```
┌─────────────────────────────────────┐
│   Controller Layer (Presentation)   │
│   - REST endpoints                  │
│   - Request/Response DTOs          │
│   - Validation                     │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│   Service Layer (Business Logic)    │
│   - Business rules                  │
│   - Transaction management          │
│   - Orchestration                   │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│   Repository Layer (Data Access)    │
│   - JPA queries                     │
│   - Custom queries                  │
│   - Returns entities                │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│   Database Layer                    │
└─────────────────────────────────────┘
```

**Best Practices:**
- ✅ Controllers should be thin - only handle HTTP concerns
- ✅ Business logic belongs in Service layer
- ✅ Services work with entities, Controllers work with DTOs
- ✅ Use MapStruct for entity ↔ DTO mapping
- ✅ Never expose entities directly from controllers

### 1.2 Package Structure

```
com.nicecommerce
├── accounts/
│   ├── controller/     # REST endpoints
│   ├── service/        # Business logic
│   ├── repository/    # Data access
│   ├── entity/         # JPA entities
│   ├── dto/            # Data transfer objects
│   └── mapper/         # MapStruct mappers
├── products/
│   └── ...
├── orders/
│   └── ...
└── core/
    ├── config/         # Configuration classes
    ├── exception/      # Exception handling
    └── resilience/     # Resilience patterns
```

---

## 2. API Design

### 2.1 Contract-First Development

**Always define API contract before implementation:**

1. **Define OpenAPI specification** (`openapi.yaml`)
2. **Generate code from contract** (OpenAPI Generator)
3. **Implement business logic**
4. **Validate against contract** (Contract tests)

**Benefits:**
- ✅ API consistency
- ✅ Early validation
- ✅ Client code generation
- ✅ Documentation as code

### 2.2 RESTful Design

**Principles:**
- Use HTTP methods correctly (GET, POST, PUT, DELETE, PATCH)
- Use proper HTTP status codes
- Version your APIs (`/api/v1/...`)
- Use plural nouns for resources (`/products`, `/orders`)
- Use query parameters for filtering, pagination, sorting
- Return consistent response formats

**Example:**
```java
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    
    @GetMapping
    public ResponseEntity<ProductListResponse> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        // Implementation
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) {
        // Implementation
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> createProduct(
            @Valid @RequestBody CreateProductRequest request) {
        // Implementation
    }
}
```

### 2.3 DTO Pattern

**Why DTOs?**
- ✅ Hide internal entity structure
- ✅ Security (don't expose sensitive fields)
- ✅ Performance (only send needed data)
- ✅ Versioning (different DTOs for different API versions)
- ✅ Validation at API boundary

**Implementation with MapStruct:**
```java
@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(source = "category.name", target = "categoryName")
    ProductDTO toDTO(Product product);
    
    List<ProductDTO> toDTOList(List<Product> products);
}
```

---

## 3. Data Access

### 3.1 Repository Pattern

**Best Practices:**
- ✅ Use Spring Data JPA repositories
- ✅ Use custom queries for complex operations
- ✅ Use `@Query` for JPQL or native queries
- ✅ Use `@EntityGraph` to avoid N+1 queries
- ✅ Use pagination for large datasets

**Example:**
```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    @Query("SELECT p FROM Product p " +
           "LEFT JOIN FETCH p.category " +
           "WHERE p.isActive = true")
    Page<Product> findActiveProducts(Pageable pageable);
    
    @EntityGraph(attributePaths = {"category", "images"})
    Optional<Product> findById(Long id);
}
```

### 3.2 Query Optimization

**Avoid N+1 Query Problem:**
```java
// BAD: N+1 queries
List<Order> orders = orderRepository.findAll();
orders.forEach(order -> order.getItems().size()); // Triggers lazy loading

// GOOD: Single query with JOIN FETCH
@Query("SELECT o FROM Order o LEFT JOIN FETCH o.items")
List<Order> findAllWithItems();
```

**Use Batch Processing:**
```java
@Modifying
@Query(value = "INSERT INTO products (name, price) VALUES (:name, :price)",
       nativeQuery = true)
void batchInsert(@Param("name") String name, @Param("price") BigDecimal price);
```

### 3.3 Connection Pooling

**Configure HikariCP:**
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

---

## 4. Security

### 4.1 Authentication

**Firebase Authentication:**
```java
@Component
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain) {
        String token = extractToken(request);
        if (token != null) {
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(token);
            // Set authentication
        }
        filterChain.doFilter(request, response);
    }
}
```

### 4.2 Authorization

**Method-level security:**
```java
@PreAuthorize("hasRole('ADMIN')")
public ProductDTO createProduct(CreateProductRequest request) {
    // Implementation
}
```

### 4.3 Rate Limiting

**Implement rate limiting:**
```java
@Component
public class RateLimitingFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(...) {
        String key = getRateLimitKey(request);
        String count = redisTemplate.opsForValue().get(key);
        
        if (count != null && Integer.parseInt(count) >= limit) {
            response.setStatus(429);
            return;
        }
        
        redisTemplate.opsForValue().increment(key);
        filterChain.doFilter(request, response);
    }
}
```

---

## 5. Performance

### 5.1 Caching Strategy

**Multi-level caching:**
```java
@Service
public class ProductService {
    
    @Cacheable(value = "products", key = "#id")
    public ProductDTO findById(Long id) {
        // Implementation
    }
    
    @CacheEvict(value = "products", key = "#id")
    public ProductDTO update(Long id, UpdateProductRequest request) {
        // Implementation
    }
}
```

**Cache configuration:**
```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(2))
            .serializeKeysWith(...)
            .serializeValuesWith(...);
        
        return RedisCacheManager.builder(factory)
            .cacheDefaults(config)
            .build();
    }
}
```

### 5.2 Database Optimization

**Indexes:**
```java
@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_product_name", columnList = "name"),
    @Index(name = "idx_product_category", columnList = "category_id")
})
public class Product {
    // ...
}
```

**Batch operations:**
```java
@Transactional
public void batchCreate(List<Product> products) {
    int batchSize = 50;
    for (int i = 0; i < products.size(); i += batchSize) {
        List<Product> batch = products.subList(i, Math.min(i + batchSize, products.size()));
        productRepository.saveAll(batch);
        productRepository.flush();
    }
}
```

---

## 6. Testing

### 6.1 Testing Pyramid

```
        ┌─────────────┐
        │     E2E     │  ← Few, slow, expensive
        └─────────────┘
     ┌───────────────────┐
     │   Integration     │  ← Some, medium speed
     └───────────────────┘
┌─────────────────────────────┐
│        Unit Tests           │  ← Many, fast, cheap
└─────────────────────────────┘
```

### 6.2 Unit Tests

**Test business logic in isolation:**
```java
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    
    @Mock
    private ProductRepository productRepository;
    
    @InjectMocks
    private ProductService productService;
    
    @Test
    void shouldFindProductById() {
        // Given
        when(productRepository.findById(1L))
            .thenReturn(Optional.of(testProduct));
        
        // When
        ProductDTO result = productService.findById(1L);
        
        // Then
        assertThat(result).isNotNull();
        verify(productRepository).findById(1L);
    }
}
```

### 6.3 Integration Tests

**Test with real database:**
```java
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProductIntegrationTest {
    
    @Autowired
    private ProductService productService;
    
    @Test
    void shouldCreateAndRetrieveProduct() {
        // Test with real database
    }
}
```

### 6.4 Controller Tests

**Test REST endpoints:**
```java
@WebMvcTest(ProductController.class)
class ProductControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ProductService productService;
    
    @Test
    void shouldGetProducts() throws Exception {
        mockMvc.perform(get("/api/v1/products"))
            .andExpect(status().isOk());
    }
}
```

---

## 7. Observability

### 7.1 Structured Logging

**Use structured logging:**
```java
@Service
@Slf4j
public class ProductService {
    
    public ProductDTO findById(Long id) {
        log.info("Fetching product - productId: {}, operation: findById", id);
        
        try {
            Product product = productRepository.findById(id).orElseThrow();
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

### 7.2 Metrics

**Collect metrics:**
```java
@Service
public class ProductService {
    
    private final MeterRegistry meterRegistry;
    
    public ProductDTO findById(Long id) {
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            // Implementation
        } finally {
            sample.stop(Timer.builder("product.fetch.duration")
                .tag("operation", "findById")
                .register(meterRegistry));
        }
    }
}
```

### 7.3 Distributed Tracing

**Add tracing:**
```java
@Service
public class OrderService {
    
    private final Tracer tracer;
    
    public Order createOrder(CreateOrderRequest request) {
        Span span = tracer.nextSpan()
            .name("create-order")
            .tag("userId", request.getUserId().toString())
            .start();
        
        try (Tracer.SpanInScope ws = tracer.withSpanInScope(span)) {
            // Implementation
        } finally {
            span.end();
        }
    }
}
```

---

## 8. Resilience

### 8.1 Circuit Breaker

**Protect against cascading failures:**
```java
@Service
public class PaymentService {
    
    @CircuitBreaker(name = "payment-service", fallbackMethod = "processPaymentFallback")
    public CompletableFuture<PaymentResult> processPayment(Long orderId) {
        // Call external service
    }
    
    private CompletableFuture<PaymentResult> processPaymentFallback(
            Long orderId, Exception e) {
        // Fallback logic
        return CompletableFuture.completedFuture(
            PaymentResult.builder()
                .success(false)
                .message("Payment service temporarily unavailable")
                .build());
    }
}
```

### 8.2 Retry

**Handle transient failures:**
```java
@Retry(name = "payment-service")
public PaymentResult processPayment(Long orderId) {
    // Implementation with automatic retry
}
```

### 8.3 Idempotency

**Prevent duplicate operations:**
```java
@Transactional
public Order createOrder(Long userId, String idempotencyKey) {
    if (idempotencyKey != null) {
        String existingOrderId = redisTemplate.opsForValue()
            .get("idempotency:order:" + idempotencyKey);
        
        if (existingOrderId != null) {
            return orderRepository.findById(Long.parseLong(existingOrderId))
                .orElseThrow();
        }
    }
    
    Order order = new Order();
    order = orderRepository.save(order);
    
    if (idempotencyKey != null) {
        redisTemplate.opsForValue().set(
            "idempotency:order:" + idempotencyKey,
            order.getId().toString(),
            Duration.ofHours(24));
    }
    
    return order;
}
```

---

## 9. Documentation

### 9.1 Code Documentation

**Document classes and methods:**
```java
/**
 * Product Service
 * 
 * Business logic for product operations.
 * Implements caching, observability, and best practices.
 * 
 * @author NiceCommerce Team
 */
@Service
public class ProductService {
    
    /**
     * Find product by ID with caching
     * 
     * @param id Product ID
     * @return ProductDTO
     * @throws ResourceNotFoundException if product not found
     */
    @Cacheable(value = "products", key = "#id")
    public ProductDTO findById(Long id) {
        // Implementation
    }
}
```

### 9.2 API Documentation

**Use OpenAPI/Swagger:**
```yaml
openapi: 3.0.3
info:
  title: NiceCommerce Mobile API
  version: 1.0.0

paths:
  /products:
    get:
      summary: List products
      responses:
        '200':
          description: Successful response
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ProductListResponse'
```

---

## 🎯 Key Takeaways

1. **Contract-First**: Always define API contracts before implementation
2. **Layered Architecture**: Clear separation of concerns
3. **DTO Pattern**: Use DTOs to separate API from internal structure
4. **Caching**: Implement multi-level caching for performance
5. **Security**: OAuth2/JWT, rate limiting, input validation
6. **Observability**: Structured logging, metrics, distributed tracing
7. **Resilience**: Circuit breakers, retries, timeouts, idempotency
8. **Testing**: Comprehensive test coverage (unit, integration, e2e)
9. **Performance**: Query optimization, connection pooling, batch processing
10. **Documentation**: Code comments, API documentation, README files

---

**Good luck with your interview! 🚀**

