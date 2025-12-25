# Django to Spring Boot Migration Notes

This document outlines the key differences and migration decisions when converting from Django to Spring Boot.

## Architecture Differences

### Django vs Spring Boot

| Aspect | Django | Spring Boot |
|--------|--------|-------------|
| Language | Python | Java |
| Framework | Django (MVC) | Spring Boot (MVC) |
| ORM | Django ORM | JPA/Hibernate |
| Authentication | Django Sessions | JWT Tokens |
| Templates | Django Templates | REST API (JSON) |
| Admin | Django Admin | Custom Admin API |

## Key Migration Decisions

### 1. Authentication

**Django**: Session-based authentication with cookies
**Spring Boot**: JWT token-based authentication (stateless)

- **Reason**: Better for REST APIs, microservices, and scalability
- **Implementation**: Spring Security with JWT tokens

### 2. Data Models

**Django**: Models with JSONField for flexible data
**Spring Boot**: JPA Entities with `@JdbcTypeCode(SqlTypes.JSON)`

- **Reason**: Maintains same flexibility while using type-safe Java
- **Note**: JSON fields are stored as JSON in MySQL (MySQL 5.7+)

### 3. Business Logic

**Django**: Views and Services
**Spring Boot**: Controllers (REST) and Services

- **Pattern**: Controller → Service → Repository
- **Separation**: Clear separation of concerns

### 4. Configuration

**Django**: `settings.py` files
**Spring Boot**: `application.yml` with profiles

- **Profiles**: dev, prod for environment-specific config
- **GCP**: Production profile configured for Cloud SQL and Storage

### 5. Caching

**Django**: Built-in cache framework
**Spring Boot**: Spring Cache with Redis

- **Implementation**: `@Cacheable` annotations
- **Backend**: Redis (can fallback to in-memory)

## Entity Mapping

### User Model

```python
# Django
class User(AbstractUser, TimeStampedModel):
    email = models.EmailField(unique=True)
    role = models.CharField(max_length=20, choices=ROLE_CHOICES)
```

```java
// Spring Boot
@Entity
public class User extends BaseEntity implements UserDetails {
    @Email
    @Column(unique = true)
    private String email;
    
    @Enumerated(EnumType.STRING)
    private UserRole role;
}
```

### Product Model

```python
# Django
class Product(TimeStampedModel):
    sizes = models.JSONField(default=dict)
    images = models.JSONField(default=list)
```

```java
// Spring Boot
@Entity
public class Product extends BaseEntity {
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Integer> sizes;
    
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> images;
}
```

## API Endpoints

### Authentication

**Django**: `/accounts/login/` (form-based)
**Spring Boot**: `POST /api/auth/login` (JSON)

### Products

**Django**: `/products/` (HTML templates)
**Spring Boot**: `GET /api/products` (JSON)

## Database

### Migrations

**Django**: `python manage.py makemigrations` and `migrate`
**Spring Boot**: JPA `ddl-auto` or Flyway/Liquibase

- **Development**: `ddl-auto: update` (auto-update schema)
- **Production**: `ddl-auto: validate` (validate only, use migrations)

### Connection

**Django**: MySQL via `mysqlclient`
**Spring Boot**: MySQL via JDBC with HikariCP connection pool

## Security

### Password Hashing

**Django**: PBKDF2 (default)
**Spring Boot**: BCrypt

Both are secure, BCrypt is more modern and widely used in Java.

### CSRF Protection

**Django**: CSRF tokens for forms
**Spring Boot**: JWT tokens (stateless, no CSRF needed for REST APIs)

## Deployment

### Django

- PythonAnywhere, Heroku, or self-hosted
- Gunicorn + Nginx

### Spring Boot

- Cloud Run, App Engine, or self-hosted
- Embedded Tomcat (included) or external Tomcat

## Testing

**Django**: `pytest` or `unittest`
**Spring Boot**: JUnit 5 + Mockito

## Performance Considerations

1. **Connection Pooling**: HikariCP (default, very fast)
2. **Caching**: Redis for session and data caching
3. **JSON Fields**: MySQL JSON type for efficient querying
4. **Lazy Loading**: JPA lazy loading for relationships

## GCP Integration

### Cloud SQL

- Uses Cloud SQL Proxy for secure connections
- Connection string format: `jdbc:mysql:///database?cloudSqlInstance=...`

### Cloud Storage

- For product images and media files
- Replaces local file storage in production

### Secret Manager

- For sensitive configuration (API keys, passwords)
- Integrated via Spring Cloud GCP

## Next Steps

1. Complete remaining service implementations
2. Add comprehensive unit tests
3. Set up CI/CD pipeline
4. Configure monitoring and logging
5. Performance testing and optimization

---

**Note**: This migration maintains feature parity with the Django version while leveraging Spring Boot's strengths for enterprise Java applications.

