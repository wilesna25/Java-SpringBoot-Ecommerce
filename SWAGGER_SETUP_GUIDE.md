# 🚀 Swagger/OpenAPI Setup Guide - Expert Level

> **Complete guide for accessing and using Swagger UI in the NiceCommerce API**

---

## 📋 Overview

This project uses **SpringDoc OpenAPI 3** (formerly Swagger) for API documentation. The configuration is set up at an expert level with:

- ✅ Comprehensive API documentation
- ✅ Firebase Authentication integration
- ✅ Interactive API testing
- ✅ Multiple server environments
- ✅ Security schemes
- ✅ Request/Response examples
- ✅ Custom styling

---

## 🔗 Accessing Swagger UI

### Local Development

Once the application is running, access Swagger UI at:

```
http://localhost:8080/swagger-ui.html
```

Or the newer URL:

```
http://localhost:8080/swagger-ui/index.html
```

### API Documentation (JSON)

The OpenAPI specification in JSON format:

```
http://localhost:8080/api-docs
```

Or the v3 format:

```
http://localhost:8080/v3/api-docs
```

---

## 🔐 Authentication in Swagger UI

### Firebase Authentication

1. **Get Firebase ID Token** from your mobile app or Firebase Console
2. **Click "Authorize"** button in Swagger UI (top right)
3. **Enter your Firebase ID token** in the "Value" field
4. **Click "Authorize"** and then "Close"
5. Now all authenticated endpoints will include the token automatically

### Example Token Format

```
eyJhbGciOiJSUzI1NiIsImtpZCI6IjEyMzQ1Njc4OTAiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20vbm9tZS1jb21tZXJjZSIsImF1ZCI6Im5vbWUtY29tbWVyY2UiLCJleHAiOjE2NDIyNDgwMDAsImlhdCI6MTY0MjI0NDQwMCwidWlkIjoiMTIzNDU2Nzg5MCJ9...
```

---

## 📚 API Documentation Features

### 1. Interactive Testing

- **Try It Out**: Click "Try it out" on any endpoint
- **Fill Parameters**: Enter required/optional parameters
- **Execute**: Click "Execute" to send the request
- **View Response**: See the actual response from the server

### 2. Request/Response Examples

Each endpoint includes:
- Request body examples
- Response examples for success and error cases
- Parameter descriptions
- Validation rules

### 3. Multiple Server Environments

Switch between:
- **Production**: `https://api.nicecommerce.com/api/v1`
- **Development**: `https://api-dev.nicecommerce.com/api/v1`
- **Local**: `http://localhost:8080/api/v1`

---

## 🎨 Customization

### Configuration Files

1. **OpenApiConfig.java** - Main OpenAPI configuration
   - API information
   - Security schemes
   - Server URLs
   - Global settings

2. **application.yml** - SpringDoc properties
   - Swagger UI path
   - API docs path
   - UI customization
   - Package scanning

3. **swagger-ui-custom.css** - Custom styling
   - Color schemes
   - Layout adjustments
   - Branding

### Customizing API Info

Edit `OpenApiConfig.java`:

```java
@Bean
public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Your API Title")
            .version("1.0.0")
            .description("Your API description")
        );
}
```

### Customizing Swagger UI

Edit `application.yml`:

```yaml
springdoc:
  swagger-ui:
    path: /swagger-ui.html
    operations-sorter: method
    tags-sorter: alpha
    doc-expansion: none
    display-request-duration: true
```

---

## 🔧 Configuration Details

### Security Configuration

The API uses Firebase Bearer Token authentication:

```java
SecurityScheme firebaseAuth = new SecurityScheme()
    .type(SecurityScheme.Type.HTTP)
    .scheme("bearer")
    .bearerFormat("JWT")
    .description("Firebase ID Token Authentication");
```

### Endpoint Annotations

Controllers use comprehensive OpenAPI annotations:

```java
@Operation(
    summary = "List products",
    description = "Retrieve a paginated list of products",
    tags = {"Products"}
)
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Success"),
    @ApiResponse(responseCode = "400", description = "Bad request")
})
@GetMapping
public ResponseEntity<ProductListResponse> getProducts() {
    // Implementation
}
```

---

## 📖 Best Practices

### 1. Always Document Endpoints

```java
@Operation(
    summary = "Clear, concise summary",
    description = "Detailed description with examples",
    tags = {"TagName"}
)
```

### 2. Include Examples

```java
@ApiResponse(
    responseCode = "200",
    description = "Success",
    content = @Content(
        examples = @ExampleObject(
            name = "Example",
            value = "{\"id\": 1, \"name\": \"Product\"}"
        )
    )
)
```

### 3. Document Security Requirements

```java
@Operation(
    security = @SecurityRequirement(name = "FirebaseAuth")
)
```

### 4. Use Proper Tags

Group related endpoints:

```java
@Tag(name = "Products", description = "Product operations")
```

---

## 🧪 Testing with Swagger UI

### Example: Create Product

1. Navigate to **POST /api/v1/products**
2. Click **"Try it out"**
3. Click **"Authorize"** and enter Firebase token
4. Fill in the request body:

```json
{
  "name": "Test Product",
  "description": "Test Description",
  "price": 29.99,
  "categoryId": 1,
  "images": ["https://example.com/image.jpg"],
  "sizes": {
    "S": 10,
    "M": 15,
    "L": 8
  }
}
```

5. Click **"Execute"**
6. View the response

### Example: Get Products

1. Navigate to **GET /api/v1/products**
2. Click **"Try it out"**
3. Set parameters:
   - `page`: 0
   - `size`: 20
   - `search`: "shirt" (optional)
4. Click **"Execute"**
5. View paginated results

---

## 🚨 Troubleshooting

### Swagger UI Not Loading

**Problem**: `http://localhost:8080/swagger-ui.html` returns 404

**Solutions**:
1. Check if SpringDoc dependency is in `pom.xml`
2. Verify `springdoc.swagger-ui.enabled=true` in `application.yml`
3. Check security configuration allows `/swagger-ui/**` paths
4. Try alternative URL: `/swagger-ui/index.html`

### Authentication Not Working

**Problem**: Getting 401 Unauthorized even with token

**Solutions**:
1. Verify token format: `Bearer <token>` (not just `<token>`)
2. Check token expiration
3. Verify Firebase project configuration
4. Check security filter chain configuration

### Endpoints Not Showing

**Problem**: Some endpoints missing from Swagger UI

**Solutions**:
1. Check `springdoc.packages-to-scan` includes your package
2. Verify `springdoc.paths-to-match` includes your paths
3. Ensure controllers have `@RestController` annotation
4. Check for `@Hidden` annotation (hides endpoints)

---

## 📝 API Documentation Standards

### Required Annotations

Every endpoint should have:

```java
@Operation(
    summary = "...",
    description = "...",
    tags = {"..."}
)
@ApiResponses({
    @ApiResponse(responseCode = "200", ...),
    @ApiResponse(responseCode = "400", ...),
    @ApiResponse(responseCode = "401", ...),
    @ApiResponse(responseCode = "404", ...)
})
```

### DTO Documentation

DTOs should use `@Schema`:

```java
@Schema(description = "Product information")
public class ProductDTO {
    @Schema(description = "Product ID", example = "1")
    private Long id;
    
    @Schema(description = "Product name", example = "Premium T-Shirt")
    @NotBlank
    private String name;
}
```

---

## 🎯 Expert-Level Features

### 1. Multiple Security Schemes

Support both Firebase Auth and API Key:

```java
.addSecuritySchemes("FirebaseAuth", firebaseAuth)
.addSecuritySchemes("ApiKey", apiKey)
```

### 2. Server Variables

Dynamic server configuration:

```java
new Server()
    .url("{protocol}://{host}:{port}/api/v1")
    .variables(
        new ServerVariables()
            .addServerVariable("protocol", ...)
    )
```

### 3. Custom Response Headers

Document custom headers:

```java
@ApiResponse(
    responseCode = "201",
    headers = @Header(
        name = "Location",
        description = "URL of created resource"
    )
)
```

### 4. Request/Response Examples

Multiple examples per endpoint:

```java
@Content(
    examples = {
        @ExampleObject(name = "Example 1", value = "..."),
        @ExampleObject(name = "Example 2", value = "...")
    }
)
```

---

## 🔗 Useful Links

- **SpringDoc OpenAPI**: https://springdoc.org/
- **OpenAPI Specification**: https://swagger.io/specification/
- **Swagger UI**: https://swagger.io/tools/swagger-ui/
- **Firebase Auth**: https://firebase.google.com/docs/auth

---

## ✅ Checklist

- [ ] Swagger UI accessible at `/swagger-ui.html`
- [ ] API docs accessible at `/api-docs`
- [ ] All endpoints documented
- [ ] Security schemes configured
- [ ] Examples included
- [ ] Multiple server environments
- [ ] Custom styling applied
- [ ] Authentication working
- [ ] Error responses documented

---

**Happy API Testing! 🚀**

