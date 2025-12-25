# Junior Developer Onboarding Guide
## NiceCommerce Spring Boot Project

Welcome to the NiceCommerce Spring Boot project! This guide will help you understand the project structure, learn the technologies we use, and get you productive quickly.

## 📚 Table of Contents

1. [Project Overview](#project-overview)
2. [Prerequisites & Setup](#prerequisites--setup)
3. [Understanding the Architecture](#understanding-the-architecture)
4. [Project Structure Deep Dive](#project-structure-deep-dive)
5. [Key Technologies Explained](#key-technologies-explained)
6. [Development Workflow](#development-workflow)
7. [Common Tasks & Examples](#common-tasks--examples)
8. [Best Practices](#best-practices)
9. [Troubleshooting](#troubleshooting)
10. [Learning Resources](#learning-resources)

---

## 🎯 Project Overview

### What is NiceCommerce?

NiceCommerce is an e-commerce platform that allows users to:
- Browse and search products
- Add items to shopping cart
- Place orders
- Make payments
- Manage their account

### What is Spring Boot?

Spring Boot is a Java framework that makes it easy to create production-ready applications. Think of it as a toolkit that handles a lot of the complex setup for you.

**Why Spring Boot?**
- ✅ Fast development (less boilerplate code)
- ✅ Built-in server (no need to deploy to external server)
- ✅ Auto-configuration (Spring Boot configures things automatically)
- ✅ Production-ready features (security, monitoring, etc.)

### Project Status

This is a **migration from Django (Python) to Spring Boot (Java)**. The core functionality is implemented, and we're continuously adding features.

---

## 🛠️ Prerequisites & Setup

### Required Software

1. **Java 17** (or higher)
   ```bash
   # Check if Java is installed
   java -version
   # Should show: openjdk version "17" or higher
   ```

2. **Maven 3.6+** (Build tool)
   ```bash
   # Check if Maven is installed
   mvn -version
   ```

3. **MySQL 8.0+** (Database)
   ```bash
   # Check if MySQL is installed
   mysql --version
   ```

4. **IDE** (Choose one)
   - **IntelliJ IDEA** (Recommended - Community Edition is free)
   - **Eclipse** (Free)
   - **VS Code** (Free, with Java extensions)

5. **Git** (Version control)
   ```bash
   git --version
   ```

### Initial Setup Steps

#### Step 1: Clone the Repository

```bash
cd /path/to/your/workspace
git clone <repository-url>
cd nicecommerce-springboot
```

#### Step 2: Set Up Database

1. **Start MySQL** (if not running)
   ```bash
   # On Windows (if installed as service, it should auto-start)
   # On Mac/Linux:
   sudo systemctl start mysql
   # Or:
   brew services start mysql
   ```

2. **Create Database**
   ```sql
   -- Connect to MySQL
   mysql -u root -p
   
   -- Create database
   CREATE DATABASE nicecommerce CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   
   -- Create user (optional, for security)
   CREATE USER 'nicecommerce'@'localhost' IDENTIFIED BY 'your_password';
   GRANT ALL PRIVILEGES ON nicecommerce.* TO 'nicecommerce'@'localhost';
   FLUSH PRIVILEGES;
   EXIT;
   ```

#### Step 3: Configure Application

1. **Copy environment configuration** (if `.env` file exists, or create one)
   ```bash
   # Create application-local.yml for local overrides
   cp src/main/resources/application-dev.yml src/main/resources/application-local.yml
   ```

2. **Edit `application-local.yml`** with your database credentials:
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/nicecommerce
       username: nicecommerce
       password: your_password
   ```

#### Step 4: Build the Project

```bash
# Clean and build (downloads dependencies, compiles code)
mvn clean install

# This might take a few minutes the first time (downloading dependencies)
```

#### Step 5: Run the Application

```bash
# Option 1: Using Maven
mvn spring-boot:run

# Option 2: Using the JAR file (after building)
java -jar target/nicecommerce-springboot-1.0.0.jar

# Option 3: Run from IDE
# Right-click on NiceCommerceApplication.java → Run
```

#### Step 6: Verify It's Working

1. **Check the console** - You should see:
   ```
   Started NiceCommerceApplication in X.XXX seconds
   ```

2. **Test the API**:
   ```bash
   # Health check
   curl http://localhost:8080/actuator/health
   
   # Should return: {"status":"UP"}
   ```

3. **Open in browser**:
   - Health: http://localhost:8080/actuator/health
   - Products: http://localhost:8080/api/products

---

## 🏗️ Understanding the Architecture

### MVC Pattern (Model-View-Controller)

Our application follows the **MVC pattern**, but since we're building a REST API, we don't have traditional "Views". Instead:

```
┌─────────────┐
│  Controller │  ← Handles HTTP requests (API endpoints)
└──────┬──────┘
       │
       ▼
┌─────────────┐
│   Service   │  ← Business logic (what the app does)
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ Repository  │  ← Database operations (saving/loading data)
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  Database   │  ← MySQL database
└─────────────┘
```

### Request Flow Example

When a user makes a request to `GET /api/products`:

1. **Request arrives** → `ProductController`
2. **Controller calls** → `ProductService` (if needed)
3. **Service calls** → `ProductRepository`
4. **Repository queries** → MySQL Database
5. **Data flows back** → Repository → Service → Controller
6. **Controller returns** → JSON response to client

### Layered Architecture

```
┌─────────────────────────────────────┐
│         Presentation Layer          │  ← Controllers (REST APIs)
│      (What users interact with)     │
├─────────────────────────────────────┤
│         Business Logic Layer         │  ← Services (Business rules)
│      (What the app actually does)    │
├─────────────────────────────────────┤
│          Data Access Layer           │  ← Repositories (Database)
│      (How we store/retrieve data)    │
├─────────────────────────────────────┤
│            Database Layer            │  ← MySQL
│      (Where data is stored)          │
└─────────────────────────────────────┘
```

**Why this separation?**
- ✅ Easy to test each layer independently
- ✅ Easy to change one layer without affecting others
- ✅ Clear responsibilities
- ✅ Reusable business logic

---

## 📁 Project Structure Deep Dive

Let's explore the project structure:

```
nicecommerce-springboot/
├── pom.xml                          # Maven configuration (dependencies)
├── src/
│   ├── main/
│   │   ├── java/com/nicecommerce/
│   │   │   ├── NiceCommerceApplication.java  # Main entry point
│   │   │   │
│   │   │   ├── accounts/            # User authentication module
│   │   │   │   ├── entity/          # User.java, PasswordResetToken.java
│   │   │   │   ├── repository/      # UserRepository.java
│   │   │   │   ├── service/         # UserService.java
│   │   │   │   ├── controller/      # AuthController.java
│   │   │   │   └── dto/             # UserDTO.java, AuthRequest.java
│   │   │   │
│   │   │   ├── products/            # Product management
│   │   │   │   ├── entity/          # Product.java, Category.java
│   │   │   │   ├── repository/      # ProductRepository.java
│   │   │   │   └── controller/      # ProductController.java
│   │   │   │
│   │   │   ├── cart/                # Shopping cart
│   │   │   │   ├── entity/          # Cart.java
│   │   │   │   └── repository/      # CartRepository.java
│   │   │   │
│   │   │   ├── orders/               # Order management
│   │   │   │   ├── entity/          # Order.java
│   │   │   │   └── repository/      # OrderRepository.java
│   │   │   │
│   │   │   ├── payments/            # Payment processing
│   │   │   │   ├── entity/          # Payment.java
│   │   │   │   └── repository/      # PaymentRepository.java
│   │   │   │
│   │   │   ├── core/                # Shared utilities
│   │   │   │   ├── model/           # BaseEntity.java
│   │   │   │   ├── exception/       # Exception classes
│   │   │   │   └── config/          # Configuration classes
│   │   │   │
│   │   │   └── security/            # Security configuration
│   │   │       ├── JwtTokenProvider.java
│   │   │       ├── SecurityConfig.java
│   │   │       └── CustomUserDetailsService.java
│   │   │
│   │   └── resources/
│   │       ├── application.yml       # Main configuration
│   │       ├── application-dev.yml   # Development config
│   │       └── application-prod.yml  # Production config
│   │
│   └── test/                         # Unit tests (we'll add these)
│
└── README.md                          # Project documentation
```

### Key Files Explained

#### 1. `pom.xml` (Project Object Model)
- **What it is**: Maven's configuration file
- **What it does**: Lists all dependencies (libraries) the project needs
- **Think of it as**: Like `package.json` in Node.js or `requirements.txt` in Python

**Example from our pom.xml:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```
This tells Maven: "I need Spring Boot's web framework"

#### 2. `NiceCommerceApplication.java`
- **What it is**: The main entry point of the application
- **What it does**: Starts the Spring Boot application
- **Key annotation**: `@SpringBootApplication`

```java
@SpringBootApplication  // This tells Spring: "This is a Spring Boot app"
public class NiceCommerceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NiceCommerceApplication.class, args);
        // This starts the embedded server (Tomcat)
    }
}
```

#### 3. Entity Classes (e.g., `User.java`)
- **What it is**: Represents a database table
- **What it does**: Maps Java objects to database rows
- **Key annotations**: `@Entity`, `@Table`, `@Column`

```java
@Entity                    // This is a database entity
@Table(name = "users")     // Maps to "users" table
public class User extends BaseEntity {
    @Column(name = "email", unique = true)
    private String email;   // Maps to "email" column
}
```

#### 4. Repository Interfaces (e.g., `UserRepository.java`)
- **What it is**: Interface for database operations
- **What it does**: Provides methods to query the database
- **Key interface**: `JpaRepository`

```java
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring automatically creates implementation!
    Optional<User> findByEmail(String email);
    // You can call: userRepository.findByEmail("user@example.com")
}
```

#### 5. Service Classes (e.g., `UserService.java`)
- **What it is**: Contains business logic
- **What it does**: Implements what the application actually does
- **Key annotation**: `@Service`

```java
@Service  // Spring knows this is a service
public class UserService {
    public AuthResponse login(AuthRequest request) {
        // Business logic here
        // 1. Validate credentials
        // 2. Generate JWT token
        // 3. Return response
    }
}
```

#### 6. Controller Classes (e.g., `AuthController.java`)
- **What it is**: Handles HTTP requests
- **What it does**: Defines API endpoints
- **Key annotations**: `@RestController`, `@RequestMapping`, `@GetMapping`, `@PostMapping`

```java
@RestController                    // This is a REST API controller
@RequestMapping("/api/auth")      // All endpoints start with /api/auth
public class AuthController {
    
    @PostMapping("/login")          // POST /api/auth/login
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        // Handle login request
    }
}
```

#### 7. DTOs (Data Transfer Objects)
- **What it is**: Objects used to transfer data between layers
- **What it does**: Separates internal entities from API responses
- **Why use them**: Security (don't expose internal fields) and flexibility

```java
// User entity has password field (sensitive!)
// UserDTO doesn't have password (safe to send to client)
public class UserDTO {
    private Long id;
    private String email;
    // No password field!
}
```

---

## 🔧 Key Technologies Explained

### 1. Spring Boot

**What is it?**
A framework that makes Java development easier by providing:
- Auto-configuration (sets things up automatically)
- Embedded server (Tomcat included)
- Production-ready features

**Key Concepts:**

#### Dependency Injection (DI)
Spring manages object creation for you. Instead of:
```java
// Without Spring (manual)
UserService userService = new UserService();
```

You do:
```java
// With Spring (automatic)
@Autowired
private UserService userService;  // Spring creates and injects it
```

#### Annotations
Annotations tell Spring what to do:

| Annotation | Purpose | Example |
|------------|---------|---------|
| `@Component` | Marks a class as a Spring component | `@Component public class MyService {}` |
| `@Service` | Special `@Component` for services | `@Service public class UserService {}` |
| `@Repository` | Special `@Component` for repositories | `@Repository public interface UserRepo {}` |
| `@Controller` | Marks a class as a controller | `@Controller public class ProductController {}` |
| `@RestController` | `@Controller` + `@ResponseBody` | `@RestController public class AuthController {}` |
| `@Autowired` | Injects a dependency | `@Autowired private UserService userService;` |
| `@Entity` | Marks a class as a JPA entity | `@Entity public class User {}` |
| `@Table` | Specifies database table name | `@Table(name = "users")` |
| `@Column` | Specifies column mapping | `@Column(name = "email")` |

### 2. JPA (Java Persistence API) / Hibernate

**What is it?**
A way to interact with databases using Java objects instead of SQL.

**Key Concepts:**

#### Entity
A Java class that represents a database table:
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Primary key, auto-generated
    
    @Column(name = "email")
    private String email;
}
```

#### Repository
Interface for database operations:
```java
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring automatically provides:
    // - save(User user)
    // - findById(Long id)
    // - findAll()
    // - delete(User user)
    
    // You can add custom methods:
    Optional<User> findByEmail(String email);
}
```

#### Relationships
```java
// One-to-Many: One Category has many Products
@OneToMany(mappedBy = "category")
private List<Product> products;

// Many-to-One: Many Products belong to one Category
@ManyToOne
@JoinColumn(name = "category_id")
private Category category;
```

### 3. Spring Security

**What is it?**
Handles authentication (who you are) and authorization (what you can do).

**How it works in our project:**

1. **User logs in** → `POST /api/auth/login`
2. **Service validates credentials**
3. **JWT token is generated**
4. **Token is returned to client**
5. **Client sends token in header**: `Authorization: Bearer <token>`
6. **Filter validates token** on each request
7. **If valid, request proceeds**

**Key Components:**

- `JwtTokenProvider`: Creates and validates JWT tokens
- `JwtAuthenticationFilter`: Intercepts requests and validates tokens
- `SecurityConfig`: Configures security rules
- `CustomUserDetailsService`: Loads user data for authentication

### 4. JWT (JSON Web Tokens)

**What is it?**
A way to securely transmit information between parties as a JSON object.

**Structure:**
```
Header.Payload.Signature

Example:
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNjE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```

**Why use JWT?**
- ✅ Stateless (no need to store sessions in database)
- ✅ Scalable (works across multiple servers)
- ✅ Secure (signed and optionally encrypted)

### 5. Maven

**What is it?**
A build tool and dependency manager for Java projects.

**Key Commands:**

```bash
# Clean build artifacts
mvn clean

# Compile code
mvn compile

# Run tests
mvn test

# Package into JAR
mvn package

# Install to local repository
mvn install

# Run application
mvn spring-boot:run
```

**Dependency Management:**
Maven downloads dependencies from Maven Central Repository and stores them in `~/.m2/repository/`.

---

## 💻 Development Workflow

### Daily Workflow

1. **Pull latest changes**
   ```bash
   git pull origin main
   ```

2. **Create a feature branch**
   ```bash
   git checkout -b feature/add-product-search
   ```

3. **Make changes**
   - Write code
   - Test locally
   - Commit frequently

4. **Test your changes**
   ```bash
   mvn test
   mvn spring-boot:run
   ```

5. **Commit and push**
   ```bash
   git add .
   git commit -m "Add product search functionality"
   git push origin feature/add-product-search
   ```

6. **Create Pull Request**
   - Go to GitHub/GitLab
   - Create PR
   - Request review

### Code Review Checklist

Before submitting code for review:

- [ ] Code compiles without errors
- [ ] All tests pass
- [ ] Code follows project style
- [ ] Added comments for complex logic
- [ ] No hardcoded values (use configuration)
- [ ] Proper exception handling
- [ ] No sensitive data in code (passwords, API keys)

---

## 📝 Common Tasks & Examples

### Task 1: Add a New API Endpoint

**Scenario**: Add an endpoint to get user profile

**Steps:**

1. **Add method to Controller**
   ```java
   @RestController
   @RequestMapping("/api/users")
   public class UserController {
       
       @Autowired
       private UserService userService;
       
       @GetMapping("/profile")
       public ResponseEntity<UserDTO> getProfile(
           @AuthenticationPrincipal UserDetails userDetails) {
           // Get current user's email from security context
           String email = userDetails.getUsername();
           UserDTO user = userService.getCurrentUser(email);
           return ResponseEntity.ok(user);
       }
   }
   ```

2. **Add method to Service** (if needed)
   ```java
   @Service
   public class UserService {
       public UserDTO getCurrentUser(String email) {
           User user = userRepository.findByEmail(email)
               .orElseThrow(() -> new ResourceNotFoundException("User not found"));
           return mapToDTO(user);
       }
   }
   ```

3. **Test the endpoint**
   ```bash
   curl -H "Authorization: Bearer YOUR_TOKEN" \
        http://localhost:8080/api/users/profile
   ```

### Task 2: Add a New Entity

**Scenario**: Add a `Review` entity for product reviews

**Steps:**

1. **Create Entity**
   ```java
   @Entity
   @Table(name = "reviews")
   public class Review extends BaseEntity {
       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Long id;
       
       @ManyToOne
       @JoinColumn(name = "product_id")
       private Product product;
       
       @ManyToOne
       @JoinColumn(name = "user_id")
       private User user;
       
       @Column(nullable = false)
       private Integer rating;  // 1-5
       
       @Column(columnDefinition = "TEXT")
       private String comment;
   }
   ```

2. **Create Repository**
   ```java
   @Repository
   public interface ReviewRepository extends JpaRepository<Review, Long> {
       List<Review> findByProductId(Long productId);
       List<Review> findByUserId(Long userId);
   }
   ```

3. **Create Service** (optional, for business logic)
   ```java
   @Service
   public class ReviewService {
       @Autowired
       private ReviewRepository reviewRepository;
       
       public Review createReview(Review review) {
           // Add business logic (validation, etc.)
           return reviewRepository.save(review);
       }
   }
   ```

4. **Create Controller** (if exposing via API)
   ```java
   @RestController
   @RequestMapping("/api/reviews")
   public class ReviewController {
       @Autowired
       private ReviewService reviewService;
       
       @PostMapping
       public ResponseEntity<Review> createReview(@RequestBody Review review) {
           Review created = reviewService.createReview(review);
           return ResponseEntity.ok(created);
       }
   }
   ```

### Task 3: Add Validation

**Scenario**: Validate that email is required and valid

**Steps:**

1. **Add validation annotations to DTO**
   ```java
   public class SignUpRequest {
       @Email
       @NotBlank(message = "Email is required")
       private String email;
       
       @NotBlank
       @Size(min = 8, message = "Password must be at least 8 characters")
       private String password;
   }
   ```

2. **Add `@Valid` annotation in Controller**
   ```java
   @PostMapping("/signup")
   public ResponseEntity<AuthResponse> signUp(
       @Valid @RequestBody SignUpRequest request) {
       // Validation happens automatically
   }
   ```

3. **Validation errors are automatically handled** by `GlobalExceptionHandler`

### Task 4: Add Database Query

**Scenario**: Find products by price range

**Steps:**

1. **Add method to Repository**
   ```java
   @Repository
   public interface ProductRepository extends JpaRepository<Product, Long> {
       // Using method name (Spring generates query automatically)
       List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
       
       // Or using @Query annotation for custom SQL
       @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
       List<Product> findProductsInPriceRange(
           @Param("minPrice") BigDecimal minPrice,
           @Param("maxPrice") BigDecimal maxPrice);
   }
   ```

2. **Use in Service**
   ```java
   public List<Product> getProductsInPriceRange(BigDecimal min, BigDecimal max) {
       return productRepository.findByPriceBetween(min, max);
   }
   ```

### Task 5: Handle Exceptions

**Scenario**: Throw custom exception when product not found

**Steps:**

1. **Throw exception in Service**
   ```java
   public Product getProductById(Long id) {
       return productRepository.findById(id)
           .orElseThrow(() -> new ResourceNotFoundException(
               "Product not found with id: " + id));
   }
   ```

2. **Exception is automatically handled** by `GlobalExceptionHandler`
   - Returns proper HTTP status code (404)
   - Returns JSON error response

---

## ✅ Best Practices

### 1. Code Organization

**DO:**
```java
// ✅ Group related code together
// ✅ Use meaningful names
// ✅ Keep methods small and focused
public class UserService {
    public UserDTO getUserById(Long id) {
        // Single responsibility
    }
}
```

**DON'T:**
```java
// ❌ Don't put everything in one method
// ❌ Don't use vague names
public class Service {
    public Object doStuff(Long id, String thing) {
        // Too vague, does too much
    }
}
```

### 2. Naming Conventions

| Type | Convention | Example |
|------|------------|---------|
| Class | PascalCase | `UserService` |
| Method | camelCase | `getUserById` |
| Variable | camelCase | `userEmail` |
| Constant | UPPER_SNAKE_CASE | `MAX_RETRY_ATTEMPTS` |
| Package | lowercase | `com.nicecommerce.accounts` |

### 3. Comments

**DO:**
```java
/**
 * Calculates the total price of items in the cart.
 * 
 * @param cart The shopping cart
 * @return Total price as BigDecimal
 */
public BigDecimal calculateTotal(Cart cart) {
    // Implementation
}
```

**DON'T:**
```java
// This calculates total  ← Too obvious, code should be self-explanatory
public BigDecimal calculateTotal(Cart cart) {
    // ...
}
```

### 4. Exception Handling

**DO:**
```java
try {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    return user;
} catch (Exception e) {
    log.error("Error fetching user: {}", email, e);
    throw new BusinessException("Unable to fetch user");
}
```

**DON'T:**
```java
// ❌ Don't catch and ignore
try {
    // ...
} catch (Exception e) {
    // Empty catch block - bad!
}

// ❌ Don't catch generic Exception without handling
try {
    // ...
} catch (Exception e) {
    throw e;  // Just rethrowing - not helpful
}
```

### 5. Security

**DO:**
```java
// ✅ Use @PreAuthorize for method-level security
@PreAuthorize("hasRole('ADMIN')")
public void deleteUser(Long id) {
    // Only admins can delete users
}

// ✅ Never expose passwords in DTOs
public class UserDTO {
    private String email;
    // No password field!
}
```

**DON'T:**
```java
// ❌ Don't store passwords in plain text
user.setPassword(password);  // Should be hashed!

// ❌ Don't expose sensitive data
public class UserDTO {
    private String password;  // Never do this!
}
```

### 6. Database

**DO:**
```java
// ✅ Use transactions for operations that modify data
@Transactional
public Order createOrder(Order order) {
    // Multiple database operations
    orderRepository.save(order);
    inventoryService.updateStock(order);
    return order;
}

// ✅ Use pagination for large datasets
Page<Product> products = productRepository.findAll(PageRequest.of(0, 20));
```

**DON'T:**
```java
// ❌ Don't load all data at once
List<Product> allProducts = productRepository.findAll();  // Could be millions!

// ❌ Don't forget transactions for multiple operations
public void createOrder(Order order) {
    orderRepository.save(order);
    inventoryService.updateStock(order);  // If this fails, order is saved but stock isn't updated!
}
```

### 7. Testing

**DO:**
```java
// ✅ Write unit tests
@Test
public void testGetUserById() {
    // Given
    User user = new User();
    user.setId(1L);
    
    // When
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    UserDTO result = userService.getUserById(1L);
    
    // Then
    assertEquals(1L, result.getId());
}
```

**DON'T:**
```java
// ❌ Don't test implementation details
@Test
public void testGetUserById() {
    // Testing that a specific method was called - too specific!
    verify(userRepository).findById(1L);
}
```

---

## 🐛 Troubleshooting

### Problem: Application won't start

**Symptoms:**
```
Error: Port 8080 is already in use
```

**Solution:**
```bash
# Find process using port 8080
# Windows:
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Mac/Linux:
lsof -ti:8080 | xargs kill -9

# Or change port in application.yml:
server:
  port: 8081
```

### Problem: Database connection error

**Symptoms:**
```
Error: Access denied for user 'nicecommerce'@'localhost'
```

**Solution:**
1. Check database credentials in `application.yml`
2. Verify MySQL is running
3. Check user permissions:
   ```sql
   SHOW GRANTS FOR 'nicecommerce'@'localhost';
   ```

### Problem: Dependencies not downloading

**Symptoms:**
```
Error: Could not resolve dependencies
```

**Solution:**
```bash
# Clear Maven cache
rm -rf ~/.m2/repository

# Rebuild
mvn clean install -U  # -U forces update
```

### Problem: JWT token not working

**Symptoms:**
```
Error: Invalid JWT token
```

**Solution:**
1. Check token is being sent in header: `Authorization: Bearer <token>`
2. Verify `JWT_SECRET` matches in configuration
3. Check token hasn't expired (default: 24 hours)

### Problem: Changes not reflecting

**Symptoms:**
Code changes don't appear when running

**Solution:**
```bash
# Clean and rebuild
mvn clean install

# Or in IDE: Build → Rebuild Project
```

### Common IDE Issues

**IntelliJ IDEA:**
- **Problem**: "Cannot resolve symbol"
  - **Solution**: File → Invalidate Caches / Restart
- **Problem**: Maven dependencies not recognized
  - **Solution**: Right-click `pom.xml` → Maven → Reload Project

**Eclipse:**
- **Problem**: Project not building
  - **Solution**: Project → Clean → Clean all projects
- **Problem**: Maven dependencies missing
  - **Solution**: Right-click project → Maven → Update Project

---

## 📚 Learning Resources

### Official Documentation

1. **Spring Boot**
   - https://spring.io/projects/spring-boot
   - https://docs.spring.io/spring-boot/docs/current/reference/html/

2. **Spring Data JPA**
   - https://spring.io/projects/spring-data-jpa
   - https://docs.spring.io/spring-data/jpa/docs/current/reference/html/

3. **Spring Security**
   - https://spring.io/projects/spring-security
   - https://docs.spring.io/spring-security/reference/

### Tutorials & Courses

1. **Spring Boot Tutorial** (Baeldung)
   - https://www.baeldung.com/spring-boot

2. **Java Brains** (YouTube)
   - Spring Boot playlist

3. **Spring Academy** (Official)
   - https://spring.academy/

### Books

1. **"Spring Boot in Action"** by Craig Walls
2. **"Pro Spring 5"** by Iuliana Cosmina

### Practice

1. **Build a simple REST API** (CRUD operations)
2. **Add authentication** to your API
3. **Connect to a database**
4. **Add validation**
5. **Write unit tests**

### Key Concepts to Master

1. **Dependency Injection** - Understanding how Spring manages objects
2. **Annotations** - What each annotation does
3. **JPA/Hibernate** - How to work with databases
4. **REST APIs** - HTTP methods, status codes, request/response
5. **Security** - Authentication vs Authorization
6. **Testing** - Unit tests, integration tests

---

## 🎓 Tips for Success

### 1. Read the Code

- Start by reading existing code
- Understand how things are structured
- Don't be afraid to ask questions

### 2. Start Small

- Don't try to understand everything at once
- Focus on one module at a time
- Build on what you learn

### 3. Use the Debugger

- Set breakpoints in your IDE
- Step through code line by line
- See how data flows through the application

### 4. Write Tests

- Tests help you understand code
- Tests document expected behavior
- Tests catch bugs early

### 5. Ask Questions

- No question is too basic
- Better to ask than to guess
- Learn from code reviews

### 6. Practice Regularly

- Code every day (even if just 30 minutes)
- Build small projects
- Experiment with new features

### 7. Read Error Messages

- Error messages tell you what's wrong
- Stack traces show where the error occurred
- Google the error message (someone else had it too!)

---

## 🚀 Next Steps

Now that you understand the basics:

1. **Explore the codebase**
   - Read through existing controllers
   - Understand service implementations
   - Look at entity relationships

2. **Make your first change**
   - Add a simple endpoint
   - Test it locally
   - Get it reviewed

3. **Learn continuously**
   - Read Spring Boot documentation
   - Practice with small projects
   - Ask questions

4. **Contribute**
   - Fix bugs
   - Add features
   - Improve documentation

---

## 📞 Getting Help

### When You're Stuck

1. **Check the documentation** (this guide, README, code comments)
2. **Search existing code** (someone might have done something similar)
3. **Google the error message**
4. **Ask your team** (Slack, email, in-person)
5. **Check Stack Overflow**

### What to Include When Asking for Help

- What you're trying to do
- What you've tried
- Error messages (full stack trace)
- Relevant code snippets
- What you expected to happen
- What actually happened

---

## ✅ Checklist: Am I Ready?

Before you start coding independently, make sure you can:

- [ ] Set up the project locally
- [ ] Run the application
- [ ] Understand the project structure
- [ ] Know what each layer does (Controller, Service, Repository)
- [ ] Can read and understand existing code
- [ ] Know how to add a new endpoint
- [ ] Understand basic Spring annotations
- [ ] Can debug using IDE
- [ ] Know how to test your changes

---

**Remember**: Every expert was once a beginner. Take your time, ask questions, and don't be afraid to make mistakes. That's how you learn! 🎉

Good luck, and welcome to the team! 🚀

