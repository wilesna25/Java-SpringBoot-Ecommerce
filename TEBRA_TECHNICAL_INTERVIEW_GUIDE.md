# 🎯 Tebra Senior Backend Engineer - Technical Interview Preparation Guide

> **Comprehensive guide for Java/Spring Boot Mobile API Backend Engineer position**  
> **Expert-level concepts, diagrams, code samples, and best practices**

---

## 📋 Table of Contents

1. [Architecture Overview](#1-architecture-overview)
2. [Contract-First API Development](#2-contract-first-api-development)
3. [Layered Architecture & DTO Mapping](#3-layered-architecture--dto-mapping)
4. [Caching Strategies](#4-caching-strategies)
5. [Security & Authentication](#5-security--authentication)
6. [Observability & Monitoring](#6-observability--monitoring)
7. [Resilience Patterns](#7-resilience-patterns)
8. [Performance Optimization](#8-performance-optimization)
9. [Testing Strategy](#9-testing-strategy)
10. [CI/CD Pipeline](#10-cicd-pipeline)

---

## 1. Architecture Overview

### 1.1 System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                        Mobile Clients                               │
│              (iOS / Android Applications)                            │
└────────────────────┬────────────────────────────────────────────────┘
                     │
                     │ HTTPS / REST API
                     │
┌────────────────────▼────────────────────────────────────────────────┐
│                    API Gateway / Load Balancer                      │
│                    (Rate Limiting, SSL Termination)                 │
└────────────────────┬────────────────────────────────────────────────┘
                     │
                     │
┌────────────────────▼────────────────────────────────────────────────┐
│              Spring Boot Mobile API Service                          │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │  Presentation Layer (Controllers)                             │  │
│  │  - REST Endpoints                                             │  │
│  │  - Request/Response Validation                               │  │
│  │  - Exception Handling                                         │  │
│  └────────────────────┬──────────────────────────────────────────┘  │
│                       │                                              │
│  ┌────────────────────▼──────────────────────────────────────────┐  │
│  │  Business Logic Layer (Services)                              │  │
│  │  - Domain Logic                                               │  │
│  │  - Transaction Management                                     │  │
│  │  - Business Rules                                             │  │
│  └────────────────────┬──────────────────────────────────────────┘  │
│                       │                                              │
│  ┌────────────────────▼──────────────────────────────────────────┐  │
│  │  Data Access Layer (Repositories)                             │  │
│  │  - JPA/Hibernate                                              │  │
│  │  - Custom Queries                                             │  │
│  │  - Caching                                                    │  │
│  └────────────────────┬──────────────────────────────────────────┘  │
└───────────────────────┼──────────────────────────────────────────────┘
                        │
        ┌───────────────┼───────────────┬───────────────┐
        │               │               │               │
┌───────▼──────┐ ┌──────▼──────┐ ┌──────▼──────┐ ┌──────▼──────┐
│   MySQL      │ │   Redis     │ │  Firebase   │ │  External   │
│  (Cloud SQL) │ │   (Cache)   │ │   (Auth)    │ │   APIs      │
└──────────────┘ └─────────────┘ └─────────────┘ └─────────────┘
```

### 1.2 Request Flow Diagram

```
┌──────────┐
│  Client  │
└────┬─────┘
     │ 1. HTTP Request
     │    POST /api/products
     │    Headers: Authorization, Content-Type
     ▼
┌─────────────────────────────────────┐
│  Security Filter Chain              │
│  - Firebase Token Validation       │
│  - Rate Limiting                   │
│  - CORS                            │
└────┬────────────────────────────────┘
     │ 2. Authenticated Request
     ▼
┌─────────────────────────────────────┐
│  Controller Layer                   │
│  - @RestController                  │
│  - Request Validation (@Valid)     │
│  - DTO Mapping                      │
└────┬────────────────────────────────┘
     │ 3. Service Call
     ▼
┌─────────────────────────────────────┐
│  Service Layer                      │
│  - Business Logic                   │
│  - Transaction Management           │
│  - Cache Check                      │
└────┬────────────────────────────────┘
     │ 4. Data Access
     ▼
┌─────────────────────────────────────┐
│  Repository Layer                   │
│  - JPA Query                        │
│  - Cache Write                      │
└────┬────────────────────────────────┘
     │ 5. Database Query
     ▼
┌─────────────────────────────────────┐
│  Database (MySQL)                    │
└────┬────────────────────────────────┘
     │ 6. Response
     ▼
┌─────────────────────────────────────┐
│  Response Flow (Reverse)           │
│  Repository → Service → Controller │
│  - DTO Mapping                      │
│  - JSON Serialization                │
└────┬────────────────────────────────┘
     │ 7. HTTP Response
     ▼
┌──────────┐
│  Client  │
└──────────┘
```

### 1.3 Component Interaction Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                    Mobile API Service                       │
│                                                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐    │
│  │ Product      │  │ Order        │  │ User         │    │
│  │ Controller   │  │ Controller   │  │ Controller   │    │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘    │
│         │                  │                  │            │
│  ┌──────▼───────┐  ┌──────▼───────┐  ┌──────▼───────┐    │
│  │ Product      │  │ Order        │  │ User         │    │
│  │ Service      │  │ Service      │  │ Service      │    │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘    │
│         │                  │                  │            │
│  ┌──────▼──────────────────▼──────────────────▼───────┐    │
│  │         Shared Infrastructure                      │    │
│  │  - Cache Manager (Redis)                          │    │
│  │  - Message Queue (Kafka)                          │    │
│  │  - Circuit Breaker                                │    │
│  │  - Retry Mechanism                                │    │
│  └───────────────────────────────────────────────────┘    │
│                                                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐    │
│  │ Product      │  │ Order        │  │ User         │    │
│  │ Repository   │  │ Repository   │  │ Repository   │    │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘    │
└─────────┼──────────────────┼──────────────────┼────────────┘
          │                  │                  │
          └──────────────────┼──────────────────┘
                             │
                    ┌────────▼────────┐
                    │   MySQL DB      │
                    └─────────────────┘
```

---

## 2. Contract-First API Development

### 2.1 What is Contract-First Development?

**Contract-First** means defining the API contract (OpenAPI/Swagger specification) **before** implementing the code. This ensures:
- ✅ API consistency across teams
- ✅ Early validation of API design
- ✅ Automatic client code generation
- ✅ Documentation as code
- ✅ Backward compatibility

### 2.2 Contract-First Flow Diagram

```
┌─────────────────────────────────────────────────────────────┐
│  Step 1: Define API Contract                                │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  OpenAPI 3.0 Specification (YAML/JSON)                │  │
│  │  - Endpoints                                           │  │
│  │  - Request/Response Schemas                            │  │
│  │  - Authentication                                      │  │
│  │  - Examples                                            │  │
│  └───────────────────────────────────────────────────────┘  │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│  Step 2: Generate Code from Contract                        │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  OpenAPI Generator                                     │  │
│  │  - DTOs (Data Transfer Objects)                        │  │
│  │  - Controller Interfaces                               │  │
│  │  - Validation Annotations                              │  │
│  └───────────────────────────────────────────────────────┘  │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│  Step 3: Implement Business Logic                            │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  Implement Controller Interface                        │  │
│  │  - Service Layer Integration                           │  │
│  │  - Error Handling                                          │  │
│  │  - Mapping (Entity ↔ DTO)                              │  │
│  └───────────────────────────────────────────────────────┘  │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│  Step 4: Validate Against Contract                          │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  Contract Testing                                     │  │
│  │  - Request Validation                                 │  │
│  │  - Response Validation                                │  │
│  │  - Schema Compliance                                  │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### 2.3 OpenAPI Specification Example

```yaml
openapi: 3.0.3
info:
  title: NiceCommerce Mobile API
  version: 1.0.0
  description: Mobile API for iOS and Android clients

servers:
  - url: https://api.nicecommerce.com/v1
    description: Production server
  - url: https://api-dev.nicecommerce.com/v1
    description: Development server

paths:
  /products:
    get:
      summary: List products
      operationId: listProducts
      tags:
        - Products
      parameters:
        - name: page
          in: query
          schema:
            type: integer
            default: 0
        - name: size
          in: query
          schema:
            type: integer
            default: 20
            maximum: 100
        - name: category
          in: query
          schema:
            type: string
      responses:
        '200':
          description: Successful response
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ProductListResponse'
        '400':
          $ref: '#/components/responses/BadRequest'
        '401':
          $ref: '#/components/responses/Unauthorized'

components:
  schemas:
    Product:
      type: object
      required:
        - id
        - name
        - price
      properties:
        id:
          type: integer
        name:
          type: string
        price:
          type: number
          format: decimal
        description:
          type: string
        images:
          type: array
          items:
            type: string
        sizes:
          type: object
          additionalProperties:
            type: integer

    ProductListResponse:
      type: object
      properties:
        products:
          type: array
          items:
            $ref: '#/components/schemas/Product'
        currentPage:
          type: integer
        totalPages:
          type: integer
        totalItems:
          type: integer

  securitySchemes:
    FirebaseAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT
      description: Firebase ID Token
```

### 2.4 Implementation with OpenAPI Generator

**pom.xml Configuration:**

```xml
<plugin>
    <groupId>org.openapitools</groupId>
    <artifactId>openapi-generator-maven-plugin</artifactId>
    <version>7.0.0</version>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <inputSpec>${project.basedir}/src/main/resources/api/openapi.yaml</inputSpec>
                <generatorName>spring</generatorName>
                <apiPackage>com.nicecommerce.api</apiPackage>
                <modelPackage>com.nicecommerce.api.model</modelPackage>
                <configOptions>
                    <interfaceOnly>true</interfaceOnly>
                    <useSpringBoot3>true</useSpringBoot3>
                    <useJakartaEe>true</useJakartaEe>
                </configOptions>
            </configuration>
        </execution>
    </executions>
</plugin>
```

---

## 3. Layered Architecture & DTO Mapping

### 3.1 Layered Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                        │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  Controllers (@RestController)                        │  │
│  │  - Handle HTTP requests/responses                     │  │
│  │  - Request validation                                │  │
│  │  - DTO ↔ Entity mapping                              │  │
│  │  - Exception handling                                │  │
│  └───────────────────────────────────────────────────────┘  │
└────────────────────┬────────────────────────────────────────┘
                     │
                     │ Uses DTOs
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                    Business Logic Layer                     │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  Services (@Service)                                 │  │
│  │  - Business rules                                    │  │
│  │  - Transaction management                            │  │
│  │  - Orchestration                                     │  │
│  │  - Works with Entities                               │  │
│  └───────────────────────────────────────────────────────┘  │
└────────────────────┬────────────────────────────────────────┘
                     │
                     │ Uses Entities
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                    Data Access Layer                         │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  Repositories (JpaRepository)                        │  │
│  │  - Database operations                               │  │
│  │  - Custom queries                                    │  │
│  │  - Returns Entities                                 │  │
│  └───────────────────────────────────────────────────────┘  │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                    Database Layer                             │
│                      MySQL / Cloud SQL                        │
└─────────────────────────────────────────────────────────────┘
```

### 3.2 Why DTOs? (Data Transfer Objects)

**Problems without DTOs:**
- ❌ Exposing internal entity structure
- ❌ Security risks (sensitive fields)
- ❌ Tight coupling between API and database
- ❌ Performance issues (lazy loading, circular references)
- ❌ Versioning difficulties

**Benefits with DTOs:**
- ✅ API contract stability
- ✅ Security (hide sensitive data)
- ✅ Performance (only send needed data)
- ✅ Versioning (different DTOs for different API versions)
- ✅ Validation at API boundary

### 3.3 DTO Mapping with MapStruct

**Entity (Product.java):**
```java
@Entity
@Table(name = "products")
public class Product extends BaseEntity {
    private Long id;
    private String name;
    private String slug;
    private BigDecimal price;
    private Category category;  // Lazy-loaded relationship
    private List<String> images;
    // ... other fields
}
```

**DTO (ProductDTO.java):**
```java
@Schema(description = "Product information")
public class ProductDTO {
    @Schema(description = "Product ID", example = "1")
    private Long id;
    
    @Schema(description = "Product name", example = "Premium T-Shirt")
    @NotBlank
    private String name;
    
    @Schema(description = "Product slug", example = "premium-t-shirt")
    private String slug;
    
    @Schema(description = "Product price", example = "29.99")
    @DecimalMin("0.0")
    private BigDecimal price;
    
    @Schema(description = "Category name", example = "Clothing")
    private String categoryName;  // Flattened, not full Category object
    
    @Schema(description = "Product images")
    private List<String> images;
    
    // Getters and setters
}
```

**MapStruct Mapper:**
```java
@Mapper(componentModel = "spring", 
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {
    
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(target = "category", ignore = true)  // Don't map full category
    ProductDTO toDTO(Product product);
    
    @Mapping(source = "categoryName", target = "category.name", 
             qualifiedByName = "categoryFromName")
    Product toEntity(ProductDTO dto);
    
    List<ProductDTO> toDTOList(List<Product> products);
    
    @Named("categoryFromName")
    default Category categoryFromName(String name) {
        // This would typically require a service call
        // For now, return null or use a service
        return null;
    }
}
```

**Controller Usage:**
```java
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    private final ProductMapper productMapper;
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) {
        Product product = productService.findById(id);
        ProductDTO dto = productMapper.toDTO(product);
        return ResponseEntity.ok(dto);
    }
    
    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getProducts(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<Product> products = productService.findAll(pageable);
        Page<ProductDTO> dtoPage = products.map(productMapper::toDTO);
        return ResponseEntity.ok(dtoPage);
    }
}
```

### 3.4 MapStruct Configuration

**pom.xml:**
```xml
<properties>
    <mapstruct.version>1.5.5.Final</mapstruct.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>${mapstruct.version}</version>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <annotationProcessorPaths>
                    <path>
                        <groupId>org.mapstruct</groupId>
                        <artifactId>mapstruct-processor</artifactId>
                        <version>${mapstruct.version}</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

---

## 4. Caching Strategies

### 4.1 Caching Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                    Application Layer                         │
│                                                             │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  Service Method                                      │  │
│  │  @Cacheable("products")                              │  │
│  │  public Product getProduct(Long id) {                │  │
│  │      // Check cache first                            │  │
│  │      // If miss, query DB and cache                  │  │
│  │  }                                                    │  │
│  └───────────────────────────────────────────────────────┘  │
└────────────────────┬────────────────────────────────────────┘
                     │
                     │ Cache Abstraction
                     ▼
┌─────────────────────────────────────────────────────────────┐
│              Spring Cache Abstraction                        │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  CacheManager                                         │  │
│  │  - RedisCacheManager                                 │  │
│  │  - CaffeineCacheManager                               │  │
│  │  - EhCacheManager                                     │  │
│  └───────────────────────────────────────────────────────┘  │
└────────────────────┬────────────────────────────────────────┘
                     │
        ┌────────────┼────────────┐
        │            │            │
┌───────▼──────┐ ┌──▼──────┐ ┌───▼──────┐
│    Redis     │ │ Caffeine│ │  EhCache │
│  (Distributed)│ │ (Local) │ │  (Local) │
└──────────────┘ └──────────┘ └──────────┘
```

### 4.2 Cache Patterns

#### Pattern 1: Cache-Aside (Lazy Loading)

```
┌──────────┐
│  Client  │
└────┬─────┘
     │ Request
     ▼
┌─────────────────┐
│  Service        │
│  1. Check Cache │
└────┬────────────┘
     │
     ├─── Cache Hit ────► Return Cached Data
     │
     └─── Cache Miss ────►
          │
          ▼
     ┌─────────────────┐
     │  Query Database │
     └────┬────────────┘
          │
          ▼
     ┌─────────────────┐
     │  Store in Cache │
     └────┬────────────┘
          │
          ▼
     ┌─────────────────┐
     │  Return Data    │
     └─────────────────┘
```

**Implementation:**
```java
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    
    private final ProductRepository productRepository;
    private final RedisTemplate<String, Product> redisTemplate;
    
    @Cacheable(value = "products", key = "#id")
    public Product findById(Long id) {
        log.debug("Cache miss for product: {}", id);
        return productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }
    
    @CacheEvict(value = "products", key = "#product.id")
    public Product update(Product product) {
        return productRepository.save(product);
    }
    
    @CacheEvict(value = "products", allEntries = true)
    public void clearCache() {
        log.info("Product cache cleared");
    }
}
```

#### Pattern 2: Write-Through

```
┌──────────┐
│  Client  │
└────┬─────┘
     │ Update Request
     ▼
┌─────────────────┐
│  Service        │
│  1. Update DB   │
└────┬────────────┘
     │
     ▼
┌─────────────────┐
│  Update Cache   │
└────┬────────────┘
     │
     ▼
┌─────────────────┐
│  Return Success │
└─────────────────┘
```

**Implementation:**
```java
@CachePut(value = "products", key = "#product.id")
public Product save(Product product) {
    Product saved = productRepository.save(product);
    // Cache is automatically updated by @CachePut
    return saved;
}
```

### 4.3 Redis Configuration

```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName("localhost");
        config.setPort(6379);
        config.setPassword("your-password");
        return new LettuceConnectionFactory(config);
    }
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
    
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(1))
            .serializeKeysWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer()))
            .disableCachingNullValues();
        
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)
            .withCacheConfiguration("products", 
                config.entryTtl(Duration.ofHours(2)))
            .withCacheConfiguration("categories",
                config.entryTtl(Duration.ofDays(1)))
            .transactionAware()
            .build();
    }
}
```

### 4.4 Multi-Level Caching

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    
    private final ProductRepository productRepository;
    private final CacheManager cacheManager;
    
    /**
     * Multi-level cache: L1 (Caffeine) → L2 (Redis) → Database
     */
    public Product findByIdWithMultiLevelCache(Long id) {
        // L1 Cache (Caffeine - local, fast)
        Cache l1Cache = cacheManager.getCache("products-l1");
        Product product = l1Cache.get(id, Product.class);
        if (product != null) {
            log.debug("L1 cache hit for product: {}", id);
            return product;
        }
        
        // L2 Cache (Redis - distributed)
        Cache l2Cache = cacheManager.getCache("products-l2");
        product = l2Cache.get(id, Product.class);
        if (product != null) {
            log.debug("L2 cache hit for product: {}", id);
            // Populate L1 cache
            l1Cache.put(id, product);
            return product;
        }
        
        // Database
        log.debug("Cache miss for product: {}", id);
        product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        // Populate both caches
        l1Cache.put(id, product);
        l2Cache.put(id, product);
        
        return product;
    }
}
```

---

## 5. Security & Authentication

### 5.1 Security Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                    Mobile Client                            │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  Firebase SDK                                         │  │
│  │  - User Authentication                                │  │
│  │  - ID Token Generation                               │  │
│  └───────────────────────────────────────────────────────┘  │
└────────────────────┬────────────────────────────────────────┘
                     │
                     │ ID Token (JWT)
                     ▼
┌─────────────────────────────────────────────────────────────┐
│              Spring Security Filter Chain                   │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  1. CORS Filter                                       │  │
│  │  2. Firebase Authentication Filter                   │  │
│  │     - Token Validation                               │  │
│  │     - User Extraction                                │  │
│  │  3. Authorization Filter                             │  │
│  │     - Role-based Access Control                      │  │
│  │  4. Rate Limiting Filter                            │  │
│  └───────────────────────────────────────────────────────┘  │
└────────────────────┬────────────────────────────────────────┘
                     │
                     │ Authenticated Request
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                    Controller                                │
│  - @PreAuthorize                                             │
│  - Principal                                                 │
└─────────────────────────────────────────────────────────────┘
```

### 5.2 OAuth2 / JWT Flow

```
┌──────────┐                    ┌──────────────┐
│  Client  │                    │  Firebase   │
└────┬─────┘                    └──────┬───────┘
     │                                  │
     │  1. Login Request                │
     ├─────────────────────────────────►│
     │                                  │
     │  2. ID Token                     │
     │◄─────────────────────────────────┤
     │                                  │
     │  3. API Request + ID Token       │
     ├─────────────────────────────────►│
     │                                  │
     │  4. Verify Token                 │
     │                                  │
     │  5. User Info                    │
     │◄─────────────────────────────────┤
     │                                  │
     │  6. API Response                 │
     │◄─────────────────────────────────┤
     │                                  │
```

### 5.3 Firebase Authentication Filter

```java
@Component
@RequiredArgsConstructor
@Slf4j
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {
    
    private final FirebaseAuth firebaseAuth;
    private final UserRepository userRepository;
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        String token = extractToken(request);
        
        if (token != null) {
            try {
                // Verify Firebase ID token
                FirebaseToken decodedToken = firebaseAuth.verifyIdToken(token);
                
                // Get user from database
                User user = userRepository.findByFirebaseUid(decodedToken.getUid())
                    .orElseThrow(() -> new UnauthorizedException("User not found"));
                
                // Create authentication object
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        user, null, getAuthorities(user));
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
            } catch (FirebaseAuthException e) {
                log.error("Firebase token verification failed", e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }
}
```

### 5.4 Rate Limiting

```java
@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitingFilter extends OncePerRequestFilter {
    
    private final RedisTemplate<String, String> redisTemplate;
    
    @Value("${app.rate-limit.requests-per-minute:60}")
    private int requestsPerMinute;
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        String key = getRateLimitKey(request);
        String count = redisTemplate.opsForValue().get(key);
        
        if (count == null) {
            redisTemplate.opsForValue().set(key, "1", Duration.ofMinutes(1));
        } else {
            int currentCount = Integer.parseInt(count);
            if (currentCount >= requestsPerMinute) {
                response.setStatus(HttpServletResponse.SC_TOO_MANY_REQUESTS);
                response.getWriter().write("Rate limit exceeded");
                return;
            }
            redisTemplate.opsForValue().increment(key);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getRateLimitKey(HttpServletRequest request) {
        String userId = getUserId(request);
        String endpoint = request.getRequestURI();
        return String.format("rate-limit:%s:%s", userId, endpoint);
    }
    
    private String getUserId(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User) {
            return ((User) auth.getPrincipal()).getId().toString();
        }
        return request.getRemoteAddr(); // Fallback to IP
    }
}
```

---

## 6. Observability & Monitoring

### 6.1 Observability Pillars

```
┌─────────────────────────────────────────────────────────────┐
│                    Observability                            │
│                                                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐   │
│  │   Metrics    │  │    Logs      │  │    Traces    │   │
│  │              │  │              │  │              │   │
│  │  - Counter   │  │  - Structured│  │  - Span      │   │
│  │  - Gauge     │  │  - JSON      │  │  - Trace ID  │   │
│  │  - Histogram │  │  - Context   │  │  - Parent    │   │
│  │  - Timer     │  │  - Levels    │  │  - Duration  │   │
│  └──────────────┘  └──────────────┘  └──────────────┘   │
│                                                             │
│  Tools: Prometheus, Grafana, ELK, Jaeger, Zipkin          │
└─────────────────────────────────────────────────────────────┘
```

### 6.2 Structured Logging

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    
    private final ProductRepository productRepository;
    private final MeterRegistry meterRegistry;
    
    public Product findById(Long id) {
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            log.info("Fetching product", 
                kv("productId", id),
                kv("operation", "findById"));
            
            Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found", kv("productId", id));
                    return new ResourceNotFoundException("Product not found");
                });
            
            log.info("Product fetched successfully",
                kv("productId", id),
                kv("productName", product.getName()));
            
            return product;
            
        } catch (Exception e) {
            log.error("Error fetching product",
                kv("productId", id),
                kv("error", e.getMessage()),
                e);
            throw e;
        } finally {
            sample.stop(Timer.builder("product.fetch.duration")
                .tag("operation", "findById")
                .register(meterRegistry));
        }
    }
}

// Using structured logging library (e.g., Logstash Logback Encoder)
// logback-spring.xml:
<encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
    <providers>
        <timestamp/>
        <version/>
        <logLevel/>
        <message/>
        <mdc/>
        <stackTrace/>
    </providers>
</encoder>
```

### 6.3 Distributed Tracing

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final Tracer tracer;
    private final ProductService productService;
    private final PaymentService paymentService;
    
    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        Span span = tracer.nextSpan()
            .name("create-order")
            .tag("userId", request.getUserId().toString())
            .start();
        
        try (Tracer.SpanInScope ws = tracer.withSpanInScope(span)) {
            log.info("Creating order", kv("userId", request.getUserId()));
            
            // Create order
            Order order = new Order();
            order.setUserId(request.getUserId());
            order = orderRepository.save(order);
            
            span.tag("orderId", order.getId().toString());
            
            // Add products (creates child span)
            addProductsToOrder(order, request.getProductIds());
            
            // Process payment (creates child span)
            processPayment(order, request.getPaymentInfo());
            
            log.info("Order created successfully",
                kv("orderId", order.getId()),
                kv("userId", request.getUserId()));
            
            return order;
            
        } catch (Exception e) {
            span.tag("error", true);
            span.tag("error.message", e.getMessage());
            log.error("Error creating order", e);
            throw e;
        } finally {
            span.end();
        }
    }
    
    private void addProductsToOrder(Order order, List<Long> productIds) {
        Span span = tracer.nextSpan()
            .name("add-products-to-order")
            .tag("orderId", order.getId().toString())
            .tag("productCount", String.valueOf(productIds.size()))
            .start();
        
        try (Tracer.SpanInScope ws = tracer.withSpanInScope(span)) {
            // Business logic
            for (Long productId : productIds) {
                Product product = productService.findById(productId);
                // Add to order
            }
        } finally {
            span.end();
        }
    }
}
```

### 6.4 Metrics Collection

```java
@Configuration
@RequiredArgsConstructor
public class MetricsConfig {
    
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config()
            .commonTags("application", "nicecommerce-api")
            .commonTags("environment", System.getProperty("spring.profiles.active", "dev"));
    }
    
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
    
    @Bean
    public CountedAspect countedAspect(MeterRegistry registry) {
        return new CountedAspect(registry);
    }
}

// Usage in Service
@Service
@RequiredArgsConstructor
public class ProductService {
    
    @Timed(value = "product.service", description = "Product service operations")
    @Counted(value = "product.service.calls", description = "Product service call count")
    public Product findById(Long id) {
        // Implementation
    }
}
```

---

## 7. Resilience Patterns

### 7.1 Resilience Patterns Overview

```
┌─────────────────────────────────────────────────────────────┐
│              Resilience Patterns                              │
│                                                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │    Retry     │  │   Timeout    │  │   Circuit    │     │
│  │              │  │              │  │   Breaker    │     │
│  │  - Fixed     │  │  - Request   │  │  - Open      │     │
│  │  - Exponential│  │  - Connection│  │  - Half-Open │     │
│  │  - Jitter    │  │              │  │  - Closed    │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
│                                                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │ Idempotency  │  │   Bulkhead   │  │   Fallback   │     │
│  │              │  │              │  │              │     │
│  │  - Idempotent│  │  - Thread    │  │  - Default   │     │
│  │    Key       │  │    Pool      │  │    Value     │     │
│  │  - Dedup    │  │  - Isolation │  │  - Cache     │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
```

### 7.2 Circuit Breaker Pattern

```
┌─────────────────────────────────────────────────────────────┐
│                    Circuit Breaker States                     │
│                                                               │
│  ┌──────────────┐      ┌──────────────┐      ┌──────────────┐│
│  │   CLOSED     │─────►│   OPEN       │─────►│  HALF-OPEN  ││
│  │              │      │              │      │              ││
│  │  Normal      │      │  Failing     │      │  Testing     ││
│  │  Operation   │      │  Fast Fail   │      │  Recovery    ││
│  └──────────────┘      └──────────────┘      └──────────────┘│
│         ▲                        │                  │         │
│         │                        │                  │         │
│         └────────────────────────┴──────────────────┘         │
│                        Recovery                                │
└─────────────────────────────────────────────────────────────┘
```

**Implementation with Resilience4j:**

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalPaymentService {
    
    private final WebClient paymentWebClient;
    private final CircuitBreaker circuitBreaker;
    private final Retry retry;
    private final TimeLimiter timeLimiter;
    
    @CircuitBreaker(name = "payment-service", fallbackMethod = "processPaymentFallback")
    @Retry(name = "payment-service")
    @TimeLimiter(name = "payment-service")
    public CompletableFuture<PaymentResponse> processPayment(PaymentRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            return paymentWebClient.post()
                .uri("/payments")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(PaymentResponse.class)
                .block(Duration.ofSeconds(5));
        });
    }
    
    private CompletableFuture<PaymentResponse> processPaymentFallback(
            PaymentRequest request, Exception e) {
        log.error("Payment service fallback triggered", e);
        return CompletableFuture.completedFuture(
            PaymentResponse.builder()
                .status("FAILED")
                .message("Payment service temporarily unavailable")
                .build());
    }
}

@Configuration
public class ResilienceConfig {
    
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        return CircuitBreakerRegistry.of(
            CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .slidingWindowSize(10)
                .minimumNumberOfCalls(5)
                .build());
    }
    
    @Bean
    public RetryRegistry retryRegistry() {
        return RetryRegistry.of(
            RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofSeconds(1))
                .retryExceptions(IOException.class, TimeoutException.class)
                .build());
    }
}
```

### 7.3 Idempotency Pattern

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final RedisTemplate<String, String> redisTemplate;
    
    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        String idempotencyKey = request.getIdempotencyKey();
        
        if (idempotencyKey != null) {
            // Check if request was already processed
            String existingOrderId = redisTemplate.opsForValue()
                .get("idempotency:" + idempotencyKey);
            
            if (existingOrderId != null) {
                log.info("Idempotent request detected, returning existing order",
                    kv("idempotencyKey", idempotencyKey),
                    kv("orderId", existingOrderId));
                
                return orderRepository.findById(Long.parseLong(existingOrderId))
                    .orElseThrow();
            }
        }
        
        // Create new order
        Order order = new Order();
        order.setUserId(request.getUserId());
        order = orderRepository.save(order);
        
        // Store idempotency key
        if (idempotencyKey != null) {
            redisTemplate.opsForValue().set(
                "idempotency:" + idempotencyKey,
                order.getId().toString(),
                Duration.ofHours(24));
        }
        
        return order;
    }
}
```

---

## 8. Performance Optimization

### 8.1 Database Query Optimization

**Problem: N+1 Query Problem**

```java
// BAD: N+1 queries
@GetMapping("/orders")
public List<OrderDTO> getOrders() {
    List<Order> orders = orderRepository.findAll();  // 1 query
    // For each order, fetch items (N queries)
    return orders.stream()
        .map(order -> {
            order.getItems().size();  // Triggers lazy loading
            return orderMapper.toDTO(order);
        })
        .collect(Collectors.toList());
}
```

**Solution: Eager Fetching with JOIN FETCH**

```java
// GOOD: Single query with JOIN
@Query("SELECT o FROM Order o " +
       "LEFT JOIN FETCH o.items " +
       "LEFT JOIN FETCH o.user " +
       "WHERE o.userId = :userId")
List<Order> findByUserIdWithItems(@Param("userId") Long userId);

// Or use EntityGraph
@EntityGraph(attributePaths = {"items", "user"})
List<Order> findByUserId(Long userId);
```

### 8.2 Connection Pool Optimization

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      leak-detection-threshold: 60000
```

### 8.3 Batch Processing

```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    @Modifying
    @Query(value = "INSERT INTO products (name, price, category_id) " +
           "VALUES (:name, :price, :categoryId)",
           nativeQuery = true)
    void batchInsert(@Param("name") String name,
                    @Param("price") BigDecimal price,
                    @Param("categoryId") Long categoryId);
}

@Service
public class ProductService {
    
    @Transactional
    public void batchCreateProducts(List<CreateProductRequest> requests) {
        int batchSize = 50;
        for (int i = 0; i < requests.size(); i += batchSize) {
            List<CreateProductRequest> batch = requests.subList(
                i, Math.min(i + batchSize, requests.size()));
            
            List<Product> products = batch.stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());
            
            productRepository.saveAll(products);
            productRepository.flush();  // Force flush to DB
        }
    }
}
```

---

## 9. Testing Strategy

### 9.1 Testing Pyramid

```
                    ┌─────────────┐
                    │     E2E     │  ← Few, slow, expensive
                    │   Tests     │
                    └─────────────┘
                 ┌───────────────────┐
                 │   Integration     │  ← Some, medium speed
                 │      Tests        │
                 └───────────────────┘
            ┌─────────────────────────────┐
            │        Unit Tests           │  ← Many, fast, cheap
            └─────────────────────────────┘
```

### 9.2 Test Types

1. **Unit Tests** - Test individual components in isolation
2. **Integration Tests** - Test component interactions
3. **Component Tests** - Test a complete feature/module
4. **Contract Tests** - Test API contracts (Pact)
5. **Performance Tests** - Load, stress, spike tests

---

## 10. CI/CD Pipeline

### 10.1 CI/CD Pipeline Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                    CI/CD Pipeline                            │
│                                                               │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  │
│  │   Git    │  │  Build   │  │   Test   │  │  Deploy  │  │
│  │   Push   │─►│  (Maven) │─►│  (All)   │─►│  (K8s)   │  │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘  │
│                                                               │
│  Steps:                                                       │
│  1. Code Quality (SonarQube)                                 │
│  2. Unit Tests                                                │
│  3. Integration Tests                                         │
│  4. Build Docker Image                                        │
│  5. Security Scan (Trivy)                                     │
│  6. Deploy to Staging                                         │
│  7. E2E Tests                                                 │
│  8. Deploy to Production                                      │
└─────────────────────────────────────────────────────────────┘
```

---

## 📚 Key Takeaways for Interview

1. **Contract-First**: Always define API contracts before implementation
2. **Layered Architecture**: Clear separation of concerns
3. **DTO Mapping**: Use MapStruct for type-safe mapping
4. **Caching**: Multi-level caching (L1/L2) for performance
5. **Security**: OAuth2/JWT, rate limiting, input validation
6. **Observability**: Structured logging, metrics, distributed tracing
7. **Resilience**: Circuit breakers, retries, timeouts, idempotency
8. **Testing**: Comprehensive test coverage (unit, integration, e2e)
9. **Performance**: Query optimization, connection pooling, batch processing
10. **CI/CD**: Automated testing and deployment

---

## 🎯 Interview Questions to Prepare

1. How do you design APIs for mobile clients?
2. Explain contract-first development and its benefits.
3. How do you handle API versioning?
4. Describe your caching strategy.
5. How do you ensure API security?
6. Explain distributed tracing in microservices.
7. How do you handle failures in distributed systems?
8. Describe your testing strategy.
9. How do you optimize database queries?
10. Explain your CI/CD pipeline.

---

**Good luck with your interview! 🚀**

