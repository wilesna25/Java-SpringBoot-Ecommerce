# 🛒 NiceCommerce Spring Boot

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.9+-blue.svg)](https://maven.apache.org/)
[![Firebase](https://img.shields.io/badge/Firebase-Auth-orange.svg)](https://firebase.google.com/)
[![Google Cloud](https://img.shields.io/badge/Google%20Cloud-Platform-blue.svg)](https://cloud.google.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Test Coverage](https://img.shields.io/badge/Coverage-90%25-success.svg)](TEST_COVERAGE.md)

> A modern, scalable e-commerce platform built with Spring Boot, Firebase Authentication, and Google Cloud Platform. Migrated from Django with enterprise-grade features and best practices.

## 📋 Project Description

**NiceCommerce** is a production-ready e-commerce platform that enables businesses to manage online stores with features including product catalogs, shopping carts, order processing, payment integration, and inventory management. Built with Spring Boot 3.2.0, Java 17, and Maven, the platform leverages Firebase Authentication for secure user management and Google Cloud Platform for scalable, cloud-native deployment.

**Key Features**: Firebase Auth, RESTful APIs, Cloud SQL, Cloud Storage, CI/CD Pipeline, 90%+ Test Coverage

**Tech Stack**: Java 17 • Spring Boot 3.2.0 • Maven • MySQL • Firebase • Google Cloud Platform • Jenkins • Docker

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Quick Start](#-quick-start)
- [API Documentation](#-api-documentation)
- [Deployment](#-deployment)
- [Testing](#-testing)
- [Project Structure](#-project-structure)
- [Contributing](#-contributing)
- [License](#-license)

## ✨ Features

### 🔐 Authentication & Security
- **Firebase Authentication** with Google Auth Flow
- JWT token-based authentication
- Role-based access control (RBAC)
- Secure password management
- Email verification support
- Password recovery

### 🛍️ E-Commerce Features
- Product catalog with categories
- Shopping cart (session & user-based)
- Order management with status tracking
- Payment integration (MercadoPago)
- Inventory management
- Waitlist functionality

### ☁️ Cloud-Native
- **Google Cloud Platform** optimized
- Cloud SQL for database
- Cloud Storage for media files
- Secret Manager for credentials
- Cloud Run deployment ready
- Auto-scaling and load balancing

### 🔄 CI/CD Pipeline
- **Jenkins** with expert-level configuration
- Multi-stage pipeline (Build, Test, Deploy)
- Automated testing and code coverage
- Security scanning (OWASP, Trivy)
- Blue-green deployments
- Rollback capability

### 🚀 Production Ready
- Comprehensive test coverage (90%+)
- Health checks and monitoring
- Structured logging
- Error handling and validation
- CI/CD pipeline
- Docker containerization

## 🛠️ Tech Stack

### Backend
- **Java 17** - Modern Java features
- **Spring Boot 3.2.0** - Enterprise framework
- **Spring Data JPA** - Database abstraction
- **Spring Security** - Authentication & authorization
- **Hibernate** - ORM framework
- **MySQL 8.0** - Relational database
- **Maven 3.9+** - Build tool and dependency management (Gradle not used)

### Authentication
- **Firebase Admin SDK** - Server-side authentication
- **Firebase Authentication** - User management
- **JWT** - Token-based auth

### Cloud Services
- **Google Cloud Run** - Serverless container platform
- **Cloud SQL** - Managed MySQL database
- **Cloud Storage** - Object storage
- **Secret Manager** - Secure credential storage
- **Cloud Build** - CI/CD pipeline

### Tools & Libraries
- **Maven 3.9+** - Build tool and dependency management (Gradle not used)
- **Lombok** - Boilerplate reduction
- **MapStruct** - Bean mapping
- **Jackson** - JSON processing
- **Docker** - Containerization

## 🏗️ Architecture

```
┌─────────────────────────────────────────┐
│         Client (Web/Mobile)             │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│      Firebase SDK (ID Tokens)           │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│    Spring Boot REST API                  │
│  ┌──────────────────────────────────┐   │
│  │  Controllers (REST Endpoints)    │   │
│  └──────────────┬───────────────────┘   │
│                 ▼                        │
│  ┌──────────────────────────────────┐   │
│  │  Services (Business Logic)        │   │
│  └──────────────┬───────────────────┘   │
│                 ▼                        │
│  ┌──────────────────────────────────┐   │
│  │  Repositories (Data Access)       │   │
│  └──────────────┬───────────────────┘   │
└─────────────────┼───────────────────────┘
                  │
        ┌─────────┴─────────┐
        ▼                   ▼
┌──────────────┐   ┌──────────────┐
│  Cloud SQL   │   │  Firebase    │
│   (MySQL)    │   │  (Auth)      │
└──────────────┘   └──────────────┘
```

### Design Patterns
- **Layered Architecture** - Separation of concerns
- **Repository Pattern** - Data access abstraction
- **Service Layer** - Business logic encapsulation
- **DTO Pattern** - Data transfer objects
- **Dependency Injection** - Loose coupling

## 🚀 Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+ (or use Cloud SQL)
- Docker (optional, for containerization)
- Firebase project (for authentication)

### Local Development Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/nicecommerce-springboot.git
   cd nicecommerce-springboot
   ```

2. **Configure database**
   ```bash
   # Create database
   mysql -u root -p
   CREATE DATABASE nicecommerce CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

3. **Configure Firebase**
   - Create Firebase project at [Firebase Console](https://console.firebase.google.com/)
   - Download service account key
   - Place in `src/main/resources/firebase-service-account.json`
   - See [FIREBASE_SETUP.md](FIREBASE_SETUP.md) for details

4. **Configure application**
   ```bash
   # Copy and edit configuration
   cp src/main/resources/application-dev.yml src/main/resources/application-local.yml
   # Edit application-local.yml with your settings
   ```

5. **Build and run**
   ```bash
   # Build project
   mvn clean install
   
   # Run application
   mvn spring-boot:run
   
   # Or use Docker
   docker-compose up
   ```

6. **Verify**
   ```bash
   # Health check
   curl http://localhost:8080/actuator/health
   
   # Should return: {"status":"UP"}
   ```

### Environment Variables

Create `.env` file or set environment variables:

```bash
# Database
DATABASE_URL=jdbc:mysql://localhost:3306/nicecommerce
DATABASE_USERNAME=your_username
DATABASE_PASSWORD=your_password

# Firebase
FIREBASE_PROJECT_ID=your-project-id
FIREBASE_CREDENTIALS_JSON=firebase-service-account.json

# Application
SPRING_PROFILES_ACTIVE=dev
```

## 📚 API Documentation

### Authentication Endpoints

#### Sign Up
```http
POST /api/auth/signup
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123",
  "displayName": "John Doe",
  "phoneNumber": "+1234567890"
}
```

#### Sign In
```http
POST /api/auth/signin
Content-Type: application/json

{
  "idToken": "firebase-id-token"
}
```

#### Sign Out
```http
POST /api/auth/signout/{uid}
Authorization: Bearer {firebase-id-token}
```

#### Update User
```http
PUT /api/auth/users/{uid}
Authorization: Bearer {firebase-id-token}
Content-Type: application/json

{
  "displayName": "Updated Name",
  "phoneNumber": "+9876543210"
}
```

#### Password Reset
```http
POST /api/auth/password/reset
Content-Type: application/json

{
  "email": "user@example.com"
}
```

### Product Endpoints

#### List Products
```http
GET /api/products?page=0&size=20&category=electronics&search=laptop
```

#### Get Product
```http
GET /api/products/{slug}
```

### Cart Endpoints

#### Get Cart
```http
GET /api/cart
Authorization: Bearer {firebase-id-token}
```

#### Add to Cart
```http
POST /api/cart/add
Authorization: Bearer {firebase-id-token}
Content-Type: application/json

{
  "productId": 1,
  "size": "M",
  "quantity": 2
}
```

### Order Endpoints

#### Create Order
```http
POST /api/orders
Authorization: Bearer {firebase-id-token}
Content-Type: application/json

{
  "shippingAddress": {
    "street": "123 Main St",
    "city": "New York",
    "zipCode": "10001"
  }
}
```

#### Get Orders
```http
GET /api/orders
Authorization: Bearer {firebase-id-token}
```

For complete API documentation, see [API_DOCUMENTATION.md](docs/API_DOCUMENTATION.md)

## ☁️ Deployment

### Google Cloud Platform

This project is optimized for deployment on Google Cloud Platform.

#### Quick Deploy

```bash
# Run deployment script
./scripts/deploy.sh
```

#### Manual Deployment

See [DEPLOYMENT_GUIDE_FIREBASE_GCP.md](DEPLOYMENT_GUIDE_FIREBASE_GCP.md) for detailed instructions.

#### Deployment Options

- **Cloud Run** (Recommended) - Serverless containers
- **App Engine** - Fully managed platform
- **GKE** - Kubernetes cluster
- **Compute Engine** - Virtual machines

### Docker

```bash
# Build image
docker build -t nicecommerce:latest .

# Run container
docker run -p 8080:8080 \
  -e DATABASE_URL=jdbc:mysql://host.docker.internal:3306/nicecommerce \
  -e FIREBASE_PROJECT_ID=your-project-id \
  nicecommerce:latest
```

### Docker Compose

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

## 🧪 Testing

### Run Tests

```bash
# Run all tests
mvn test

# Run with coverage
mvn clean test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

### Test Coverage

- **Unit Tests**: 90%+ coverage
- **Integration Tests**: All endpoints tested
- **Functional Tests**: Complete user flows

See [TEST_COVERAGE.md](TEST_COVERAGE.md) for details.

### Test Categories

- `FirebaseAuthServiceTest` - Firebase operations
- `UserServiceTest` - User management
- `AuthControllerTest` - API endpoints
- `AuthFlowIntegrationTest` - End-to-end flows

## 📁 Project Structure

```
nicecommerce-springboot/
├── src/
│   ├── main/
│   │   ├── java/com/nicecommerce/
│   │   │   ├── accounts/          # User authentication
│   │   │   │   ├── config/        # Firebase configuration
│   │   │   │   ├── controller/    # REST endpoints
│   │   │   │   ├── dto/           # Data transfer objects
│   │   │   │   ├── entity/        # User entities
│   │   │   │   ├── repository/    # Data access
│   │   │   │   └── service/       # Business logic
│   │   │   ├── products/          # Product management
│   │   │   ├── cart/              # Shopping cart
│   │   │   ├── orders/            # Order management
│   │   │   ├── payments/          # Payment processing
│   │   │   ├── core/              # Shared utilities
│   │   │   └── security/          # Security configuration
│   │   └── resources/
│   │       ├── application.yml    # Main configuration
│   │       ├── application-dev.yml # Development config
│   │       └── application-prod.yml # Production config
│   └── test/                      # Test files
├── scripts/                        # Deployment scripts
├── docs/                           # Documentation
├── pom.xml                         # Maven configuration
├── Dockerfile                      # Docker image
├── docker-compose.yml              # Docker Compose
└── README.md                       # This file
```

## 📖 Documentation

- [Project Description](PROJECT_DESCRIPTION.md) - **Complete project overview**
- [Tech Stack Summary](TECH_STACK_SUMMARY.md) - **Technology stack reference**
- [Quick Start Guide](JUNIOR_DEVELOPER_GUIDE.md) - For new developers
- [Maven Best Practices](MAVEN_BEST_PRACTICES.md) - Comprehensive Maven guide
- [Maven Setup](MAVEN_SETUP.md) - Maven dependency management (Gradle not used)
- [Firebase Setup](FIREBASE_SETUP.md) - Firebase configuration
- [Deployment Guide](DEPLOYMENT_GUIDE_FIREBASE_GCP.md) - GCP deployment
- [Jenkins Setup](JENKINS_SETUP_GUIDE.md) - CI/CD pipeline configuration
- [API Documentation](docs/API_DOCUMENTATION.md) - API reference
- [Test Coverage](TEST_COVERAGE.md) - Testing details
- [Migration Notes](MIGRATION_NOTES.md) - Django to Spring Boot

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines

- Follow Java coding standards
- Write unit tests for new features
- Maintain 80%+ test coverage
- Update documentation
- Follow commit message conventions

See [CONTRIBUTING.md](CONTRIBUTING.md) for detailed guidelines.

## 🔒 Security

- All secrets stored in Google Cloud Secret Manager
- Firebase handles password security
- JWT tokens for stateless authentication
- Role-based access control
- Input validation on all endpoints
- SQL injection protection (JPA)
- XSS protection
- CORS configured

**Security Issues**: Please report security vulnerabilities to security@nicecommerce.com

## 📊 Monitoring

### Health Checks

```bash
# Application health
GET /actuator/health

# Application info
GET /actuator/info

# Metrics
GET /actuator/metrics
```

### Logging

- Structured logging with SLF4J
- Cloud Logging integration
- Request/response logging
- Error tracking

### Metrics

- Request rates
- Response times
- Error rates
- Database connection pool
- JVM metrics

## 🚦 Status

- ✅ Core features implemented
- ✅ Firebase authentication integrated
- ✅ Test coverage: 90%+
- ✅ Production deployment ready
- ✅ Documentation complete
- 🔄 CI/CD pipeline configured
- 🔄 Monitoring and alerts setup

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Firebase team for authentication services
- Google Cloud Platform for infrastructure
- All contributors and maintainers

## 📞 Support

- **Documentation**: Check the [docs](docs/) folder
- **Issues**: [GitHub Issues](https://github.com/yourusername/nicecommerce-springboot/issues)
- **Discussions**: [GitHub Discussions](https://github.com/yourusername/nicecommerce-springboot/discussions)
- **Email**: support@nicecommerce.com

## 🌟 Star History

[![Star History Chart](https://api.star-history.com/svg?repos=yourusername/nicecommerce-springboot&type=Date)](https://star-history.com/#yourusername/nicecommerce-springboot&Date)

---

<div align="center">

**Built with ❤️ using Spring Boot and Google Cloud Platform**

[⭐ Star this repo](https://github.com/yourusername/nicecommerce-springboot) | [🐛 Report Bug](https://github.com/yourusername/nicecommerce-springboot/issues) | [💡 Request Feature](https://github.com/yourusername/nicecommerce-springboot/issues)

</div>
