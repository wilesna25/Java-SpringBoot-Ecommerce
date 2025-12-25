# Tech Stack Summary
## NiceCommerce Spring Boot

Quick reference for the technology stack used in the NiceCommerce project.

---

## 🎯 Core Technologies

| Category | Technology | Version | Purpose |
|----------|-----------|---------|---------|
| **Language** | Java | 17 | Application development |
| **Framework** | Spring Boot | 3.2.0 | Enterprise framework |
| **Build Tool** | Maven | 3.9+ | Dependency management |
| **Database** | MySQL | 8.0 | Data persistence |
| **ORM** | Hibernate/JPA | (via Spring) | Database abstraction |

---

## 🔐 Security & Authentication

| Technology | Purpose |
|------------|---------|
| Firebase Authentication | User authentication (Google Auth Flow) |
| Spring Security | Authorization and access control |
| JWT | Token-based authentication |
| OWASP Dependency Check | Vulnerability scanning |

---

## ☁️ Cloud & Infrastructure

| Service | Provider | Purpose |
|---------|----------|---------|
| Cloud Run | Google Cloud | Serverless container hosting |
| Cloud SQL | Google Cloud | Managed MySQL database |
| Cloud Storage | Google Cloud | Media file storage |
| Secret Manager | Google Cloud | Secure credential storage |
| Cloud Build | Google Cloud | CI/CD pipeline |

---

## 🛠️ Development Tools

| Tool | Purpose |
|------|---------|
| Lombok | Reduces boilerplate code |
| MapStruct | Type-safe bean mapping |
| Jackson | JSON serialization/deserialization |
| Docker | Containerization |
| Maven Wrapper | Consistent build environment |

---

## 🧪 Testing & Quality

| Tool | Purpose |
|------|---------|
| JUnit 5 | Unit testing framework |
| Mockito | Mocking framework |
| JaCoCo | Code coverage analysis |
| Spring Test | Integration testing |
| H2 Database | In-memory test database |

---

## 🔄 CI/CD & DevOps

| Tool | Purpose |
|------|---------|
| Jenkins | Continuous Integration/Deployment |
| GitHub Actions | Automated workflows |
| Docker | Container builds |
| Maven | Build automation |
| Cloud Build | GCP CI/CD |

---

## 📦 Key Dependencies

### Spring Boot Starters
- `spring-boot-starter-web` - REST APIs
- `spring-boot-starter-data-jpa` - Database access
- `spring-boot-starter-security` - Security
- `spring-boot-starter-validation` - Input validation
- `spring-boot-starter-cache` - Caching
- `spring-boot-starter-actuator` - Monitoring

### External Libraries
- `firebase-admin` - Firebase Authentication
- `google-cloud-storage` - Cloud Storage
- `google-cloud-secret-manager` - Secret management
- `mysql-connector-j` - MySQL driver
- `jjwt` - JWT token handling

---

## 🏗️ Architecture Patterns

- **Layered Architecture** - Separation of concerns
- **Repository Pattern** - Data access abstraction
- **Service Layer** - Business logic encapsulation
- **DTO Pattern** - Data transfer objects
- **Dependency Injection** - Spring IoC container

---

## 📱 API & Communication

- **RESTful APIs** - HTTP/JSON
- **Spring WebFlux** - Reactive HTTP client
- **Jackson** - JSON processing
- **Firebase SDK** - Client authentication

---

## 💾 Data Storage

- **MySQL** - Primary database (Cloud SQL)
- **Redis** - Caching layer (optional)
- **Cloud Storage** - Media files
- **H2** - Test database

---

## 🔍 Monitoring & Logging

- **Spring Actuator** - Health checks and metrics
- **Google Cloud Logging** - Centralized logging
- **Prometheus** - Metrics collection (via Actuator)

---

## 📋 Build & Deployment

- **Maven** - Build tool
- **Docker** - Containerization
- **Cloud Run** - Deployment platform
- **Jenkins** - CI/CD automation
- **GitHub Actions** - Additional CI/CD

---

**Last Updated**: After complete project setup  
**Status**: Production Ready

