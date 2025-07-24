# Ecom Backend

Spring Boot backend for **Ecom**, a complete e-commerce API built to practice real-world backend architecture using modern tools like Spring Security, Keycloak, AWS S3, Liquibase, and more.

---

## 📌 Description

This backend project powers a simple e-commerce system with user authentication, product management, shopping cart, checkout, and order tracking features.

It was built with a focus on learning clean architecture and applying best practices in Spring Boot projects, including:
- Role-based access control (Keycloak + JWT)
- RESTful architecture
- API versioning and Swagger documentation
- File upload to AWS S3
- PostgreSQL + Liquibase for database migrations
- DTO mapping with MapStruct

---

## 🚀 Technologies Used

- Java 21
- Spring Boot 3.3.6
- PostgreSQL
- Liquibase
- Spring Security + Keycloak (JWT)
- Spring Data JPA
- MapStruct
- AWS S3 SDK (v2)
- Feign Client (Keycloak Admin API)
- Swagger (Springdoc OpenAPI)
- JUnit 5
- Gradle

---

## 🔐 Authentication & Authorization

The project uses **Keycloak** for user authentication and authorization.

- **JWT tokens** are used for secure access to protected endpoints
- Roles:
  - `USER` – can access public APIs and manage their cart/orders
  - `ADMIN` – can manage all resources via `/api/admin/**`
- Role-based restrictions are enforced via `@PreAuthorize("hasRole(...)")` annotations

---

## 📦 Features

The backend includes:

- ✅ Public product browsing
- 👥 User and role management via Keycloak
- 🛒 Cart and checkout system with Anonymous ID for gest carts
- 📦 Order management with status control
- 📂 Category and brand management
- 🖼️ Product image uploads to **AWS S3**
- 📑 API documentation via Swagger
- 🔁 Pagination and filtering
- 🧱 Liquibase for DB versioning

---

## 📘 API Documentation

Access the full Swagger UI at:

```
http://localhost:8081/swagger-ui/index.html
```

---

## 🧪 Testing

This project includes unit tests for mappers using:

- **JUnit 5**
- **Spring Boot Test**

Tests are located in the `src/test/java` directory.

```bash
./gradlew test
```

---

## 📂 Project Structure

```
br.com.ecommerce.ecom
├── config               // Security, Swagger, S3 and RestTemplate configs
│   └── keycloak         // JWT converter
├── controller           // API endpoints
├── dto                  // DTOs for request, response, filters
├── entity               // JPA entities (Product, User, Order, etc.)
├── enums                // Domain enums
├── exception            // Global exception handling
├── factory              // Response Factory
├── mappers              // MapStruct interfaces
├── model.keycloak       // Feign models for Keycloak
├── repository           // Spring Data JPA repositories
├── service              // Business logic
├── specification        // JPA Specifications for filtering
├── util                 // Utilities
└── EcomApplication      // Spring Boot main class
```

---

## 🔧 How to Run

### 1. Clone the repository

```bash
git clone https://github.com/Matheus-Malara/ecom-backend.git
cd ecom-backend
```

### 2. Configure environment

Create a file at `src/main/resources/application.properties`:

```properties
# Server
server.port=8081

# Keycloak (JWT validation)
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://192.168.1.90:8080/realms/SpringSecurityRealm

# Keycloak Admin Client (for Feign)
keycloak.url=http://192.168.1.90:8080
keycloak.realm=SpringSecurityRealm
keycloak.client-id=ecommerce-springboot-client
keycloak.client-secret=YZXe4T9UtUgAw0WaSMjsKbfIPr8OfEIE

# PostgreSQL DB
spring.datasource.url=jdbc:postgresql://192.168.1.90:5433/ecom_db
spring.datasource.username=ecom_user
spring.datasource.password=ecom_pass

# Liquibase
spring.liquibase.enabled=true
spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.yaml

# AWS S3
aws.s3.bucket=matmlr-projectecom-product-images
```

Also export the following environment variables:

```bash
export AWS_ACCESS_KEY_ID=your_key
export AWS_SECRET_ACCESS_KEY=your_secret
export AWS_REGION=us-east-1
```

### 3. Run the project

```bash
./gradlew bootRun
```

Or run `EcomApplication.java` directly via IntelliJ.

---

## 🧠 Learning Goals

This project was built with the goal of practicing:

- Clean REST API architecture
- Spring Security + OAuth2 with Keycloak
- JWT-based role authorization
- Integration with AWS S3 (v2 SDK)
- API documentation with Swagger
- Pagination and filtering with Spring Data
- Database migrations using Liquibase
- DTO mapping with MapStruct
- Exception handling with custom response structure

---

## 🌐 Related Projects

- [🛍️ Ecom Frontend (React + Vite)](https://github.com/Matheus-Malara/ecom-frontend)
- [🛠️ Ecom Admin Dashboard (React + Vite)](https://github.com/Matheus-Malara/ecom-frontend-admin)

---

## 🛡️ Security Note

**Do not expose sensitive credentials.**  
Be sure to add `application.properties`, `.env`, and other secret files to `.gitignore`.
