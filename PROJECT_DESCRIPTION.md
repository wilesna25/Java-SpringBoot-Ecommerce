# NiceCommerce Spring Boot - Project Description

## 📝 Short Description

**NiceCommerce** is a modern, scalable e-commerce platform built with Spring Boot, featuring Firebase Authentication, Google Cloud Platform integration, and a comprehensive CI/CD pipeline. The application provides a complete online shopping experience with product catalog, shopping cart, order management, and payment processing capabilities.

---

## 🎯 Project Overview

NiceCommerce is a full-featured e-commerce solution that enables businesses to:
- Manage product catalogs with categories and inventory
- Process customer orders with status tracking
- Handle secure payments via MercadoPago integration
- Provide user authentication and account management
- Support multi-language operations (English/Spanish)
- Scale seamlessly on Google Cloud Platform

**Status**: Production-ready with 90%+ test coverage  
**Architecture**: Microservices-ready, cloud-native  
**Deployment**: Google Cloud Run, Cloud SQL, Cloud Storage

---

## 🛠️ Tech Stack

### **Backend Framework**
- **Spring Boot 3.2.0** - Enterprise Java framework
- **Java 17** - Modern Java features and performance
- **Maven 3.9+** - Build tool and dependency management

### **Database & Persistence**
- **MySQL 8.0** - Relational database (Cloud SQL in production)
- **Spring Data JPA** - Database abstraction layer
- **Hibernate** - ORM framework

### **Authentication & Security**
- **Firebase Authentication** - Google Auth Flow integration
- **Spring Security** - Authorization and access control
- **JWT Tokens** - Stateless authentication

### **Cloud Platform**
- **Google Cloud Platform**
  - Cloud Run (serverless containers)
  - Cloud SQL (managed MySQL)
  - Cloud Storage (media files)
  - Secret Manager (credentials)
  - Cloud Build (CI/CD)

### **Development Tools**
- **Lombok** - Boilerplate code reduction
- **MapStruct** - Type-safe bean mapping
- **Jackson** - JSON processing

### **Testing & Quality**
- **JUnit 5** - Unit testing framework
- **Mockito** - Mocking framework
- **JaCoCo** - Code coverage (90%+)
- **OWASP Dependency Check** - Security scanning

### **CI/CD & DevOps**
- **Jenkins** - Continuous Integration/Deployment
- **Docker** - Containerization
- **GitHub Actions** - Automated workflows
- **Maven** - Build automation

### **Additional Services**
- **Redis** - Caching and session management
- **MercadoPago** - Payment gateway integration
- **Firebase Admin SDK** - Server-side authentication

---

## 📊 Architecture

- **Pattern**: Layered Architecture (Controller → Service → Repository)
- **API Style**: RESTful APIs
- **Authentication**: Firebase ID tokens
- **Database**: MySQL with JPA/Hibernate
- **Deployment**: Containerized (Docker) on Cloud Run
- **Scaling**: Auto-scaling serverless containers

---

## 🚀 Key Features

- ✅ User authentication (Firebase)
- ✅ Product catalog management
- ✅ Shopping cart (session & user-based)
- ✅ Order processing and tracking
- ✅ Payment integration (MercadoPago)
- ✅ Inventory management
- ✅ Multi-language support
- ✅ Comprehensive test coverage
- ✅ Production-ready CI/CD pipeline

---

## 📈 Project Metrics

- **Language**: Java 17
- **Framework**: Spring Boot 3.2.0
- **Build Tool**: Maven (Gradle not used)
- **Test Coverage**: 90%+
- **Deployment**: Google Cloud Platform
- **CI/CD**: Jenkins + GitHub Actions
- **Container**: Docker

---

**Built with modern Java best practices and cloud-native architecture.**

