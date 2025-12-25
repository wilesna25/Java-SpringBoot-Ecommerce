# Quick Reference Guide
## NiceCommerce Spring Boot - Cheat Sheet

A quick reference for common tasks and commands.

## 🚀 Quick Start

```bash
# Build project
mvn clean install

# Run application
mvn spring-boot:run

# Run tests
mvn test

# Check health
curl http://localhost:8080/actuator/health
```

## 📁 Project Structure

```
accounts/     → User authentication
products/     → Product management
cart/         → Shopping cart
orders/       → Order management
payments/     → Payment processing
core/         → Shared utilities
security/     → Security configuration
```

## 🔑 Key Annotations

| Annotation | Purpose | Example |
|------------|---------|---------|
| `@Entity` | Database entity | `@Entity public class User {}` |
| `@Table` | Table name | `@Table(name = "users")` |
| `@Id` | Primary key | `@Id private Long id;` |
| `@Column` | Column mapping | `@Column(name = "email")` |
| `@Service` | Business logic | `@Service public class UserService {}` |
| `@Repository` | Data access | `@Repository public interface UserRepo {}` |
| `@RestController` | REST API | `@RestController public class Controller {}` |
| `@Autowired` | Dependency injection | `@Autowired private UserService service;` |
| `@GetMapping` | GET endpoint | `@GetMapping("/users")` |
| `@PostMapping` | POST endpoint | `@PostMapping("/users")` |
| `@RequestBody` | Request body | `@RequestBody UserDTO user` |
| `@PathVariable` | URL parameter | `@PathVariable Long id` |
| `@RequestParam` | Query parameter | `@RequestParam String name` |
| `@Valid` | Validation | `@Valid @RequestBody UserDTO user` |

## 🗄️ Repository Methods

```java
// Basic CRUD (automatically provided)
repository.save(entity)           // Create/Update
repository.findById(id)          // Read
repository.findAll()             // Read all
repository.delete(entity)         // Delete

// Custom queries (Spring generates automatically)
findByEmail(String email)
findByEmailAndActive(String email, Boolean active)
findByPriceBetween(BigDecimal min, BigDecimal max)
findByCreatedAtAfter(LocalDateTime date)

// Custom SQL
@Query("SELECT u FROM User u WHERE u.email = :email")
User findByEmail(@Param("email") String email);
```

## 🔐 Security

```java
// Get current user in controller
@GetMapping("/profile")
public ResponseEntity<UserDTO> getProfile(
    @AuthenticationPrincipal UserDetails userDetails) {
    String email = userDetails.getUsername();
    // ...
}

// Method-level security
@PreAuthorize("hasRole('ADMIN')")
public void adminOnlyMethod() { }

// Check roles
userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
```

## 📝 Common Code Patterns

### Create Entity

```java
@Entity
@Table(name = "example")
public class Example extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
}
```

### Create Repository

```java
@Repository
public interface ExampleRepository extends JpaRepository<Example, Long> {
    Optional<Example> findByName(String name);
}
```

### Create Service

```java
@Service
@RequiredArgsConstructor
public class ExampleService {
    private final ExampleRepository repository;
    
    public Example create(Example example) {
        return repository.save(example);
    }
}
```

### Create Controller

```java
@RestController
@RequestMapping("/api/examples")
@RequiredArgsConstructor
public class ExampleController {
    private final ExampleService service;
    
    @GetMapping("/{id}")
    public ResponseEntity<Example> getById(@PathVariable Long id) {
        Example example = service.findById(id);
        return ResponseEntity.ok(example);
    }
}
```

### Exception Handling

```java
// In Service
throw new ResourceNotFoundException("Resource not found");

// Automatically handled by GlobalExceptionHandler
// Returns: 404 Not Found
```

## 🧪 Testing

```java
@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserService userService;
    
    @Test
    void testGetUser() {
        UserDTO user = userService.getUserById(1L);
        assertNotNull(user);
    }
}
```

## 🔧 Configuration

### application.yml

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/nicecommerce
    username: user
    password: pass
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

app:
  jwt:
    secret: your-secret-key
    expiration: 86400000  # 24 hours
```

## 📡 API Endpoints

### Authentication

```bash
# Sign up
POST /api/auth/signup
Body: { "email": "...", "password": "..." }

# Login
POST /api/auth/login
Body: { "email": "...", "password": "..." }
Response: { "token": "...", "user": {...} }
```

### Products

```bash
# List products
GET /api/products?page=0&size=20

# Get product
GET /api/products/{slug}
```

### With Authentication

```bash
# Add header
Authorization: Bearer <token>

# Get profile
GET /api/users/profile
```

## 🐛 Common Errors

| Error | Solution |
|-------|----------|
| Port 8080 in use | Change port or kill process |
| Database connection failed | Check credentials, MySQL running |
| Cannot resolve symbol | Rebuild project, invalidate caches |
| JWT token invalid | Check secret, token expiration |
| 401 Unauthorized | Add Authorization header |

## 📦 Maven Commands

```bash
mvn clean                    # Clean build
mvn compile                  # Compile code
mvn test                      # Run tests
mvn package                   # Create JAR
mvn install                   # Install to local repo
mvn spring-boot:run          # Run application
mvn dependency:tree          # Show dependencies
```

## 🔍 Debugging

### Logging

```java
@Slf4j  // Lombok annotation
public class MyService {
    public void doSomething() {
        log.debug("Debug message");
        log.info("Info message");
        log.warn("Warning message");
        log.error("Error message", exception);
    }
}
```

### Breakpoints

1. Click left margin in IDE (red dot appears)
2. Run in debug mode
3. Step through code (F8 = step over, F7 = step into)

## 📚 Useful Links

- Spring Boot Docs: https://docs.spring.io/spring-boot/
- Spring Data JPA: https://docs.spring.io/spring-data/jpa/
- Baeldung Tutorials: https://www.baeldung.com/spring-boot
- Stack Overflow: https://stackoverflow.com/questions/tagged/spring-boot

## 💡 Pro Tips

1. **Use IDE shortcuts** - Learn keyboard shortcuts for your IDE
2. **Read error messages** - They tell you what's wrong
3. **Use debugger** - Step through code to understand flow
4. **Check logs** - Application logs show what's happening
5. **Test incrementally** - Test after each small change
6. **Commit often** - Small, frequent commits are better
7. **Ask questions** - Better to ask than waste time

---

**Remember**: When in doubt, check the full guide: `JUNIOR_DEVELOPER_GUIDE.md`

