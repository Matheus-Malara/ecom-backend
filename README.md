# 🛒 Ecom Backend

**Educational Project – Backend API for simple E-commerce**

This is a backend-only simple e-commerce system built with **Java 21** and **Spring Boot 3.3.6**, developed as a learning project to practice API design, security, and architectural patterns using modern Java frameworks.  
It supports authentication, product and cart management, order processing, DTO mapping, and basic role-based access control with `@PreAuthorize`.

---

## 🚀 Features

### ✅ Authentication
- JWT-based OAuth2 authentication using **Keycloak**
- Role-based access with `@PreAuthorize` for protected endpoints

### 🛍️ Product, Category & Brand Management
- CRUD operations for Products, Categories, and Brands
- Products support:
  - Multiple images
  - Price, flavor, active status, timestamps
  - Relations with Brand and Category
- Product filtering and pagination via `ProductFilterDTO`

### 🛒 Cart System
- Each user has one active cart (`checkedOut = false`)
- Supports:
  - Add, update, and remove items
  - Quantity handling
  - Response via DTO (`CartResponseDTO`)
- Prevents cart auto-creation on GET

### 🧾 Order System
- Converts cart into `Order` with `OrderItems`
- Endpoints for:
  - User: list own orders with pagination
  - Admin: list all orders, update order status
- Order response formatted using DTOs

### 📑 Pagination & Filters
- Fully implemented for:
  - Products
  - Categories
  - Brands
  - Orders

### 🔐 Role-based Access Control
- Endpoints protected with `@PreAuthorize`
- `USER` can manage own cart and orders
- `ADMIN` can access management endpoints

### 🧪 Unit Testing
- Unit tests using **JUnit 5**
- Currently focused on MapStruct mappers:
  - ProductMapper
  - CartMapper
  - OrderMapper

### 🧰 Back-end Tech Stack

- Java 21
- Spring Boot 3.3.6
- Spring Security (OAuth2 Resource Server)
- Spring Data JPA
- Spring Validation
- PostgreSQL
- Feign Client (for future integrations)
- MapStruct (`componentModel = "spring"`)
- JUnit 5
- Lombok
- Devtools

---

## 📂 Project Structure

src/
├── main/
│ ├── java/br/com/ecommerce/ecom/
│ │ ├── client/
│ │ ├── config/
│ │ ├── controller/
│ │ ├── dto/ (requests, responses, filters)
│ │ ├── entity/
│ │ ├── enums/
│ │ ├── exception/
│ │ ├── factory/
│ │ ├── mapper/
│ │ ├── model/
│ │ ├── repository/
│ │ ├── service/
│ │ ├── specification/
│ │ └── util/
│ └── resources/
│ └── application.properties
└── test/
└── java/br/com/ecommerce/ecom/mapper/

---

## 🛠️ Running Locally

1. Clone the project

   git clone https://github.com/Matheus-Malara/ecom-backend.git  
   cd ecom-backend

2. Configure environment

   Update `application.properties` with your PostgreSQL and Keycloak credentials.

   Example:

   spring.datasource.url=jdbc:postgresql://localhost:5432/ecom  
   spring.datasource.username=youruser  
   spring.datasource.password=yourpassword  
   spring.jpa.hibernate.ddl-auto=update  
   spring.jpa.show-sql=true  
   spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect  

   keycloak.auth-server-url=http://localhost:8080  
   keycloak.realm=your-realm  
   keycloak.resource=your-client-id  
   keycloak.credentials.secret=your-client-secret

3. (Optional) Start PostgreSQL with Docker

   docker run --name ecom-postgres -e POSTGRES_PASSWORD=yourpassword -e POSTGRES_DB=ecom -p 5432:5432 -d postgres:15

   Or use `docker-compose.yml`:

   version: '3.1'  

   services:  
     postgres:  
       image: postgres:15  
       container_name: ecom-postgres  
       restart: always  
       environment:  
         POSTGRES_DB: ecom  
         POSTGRES_USER: youruser  
         POSTGRES_PASSWORD: yourpassword  
       ports:  
         - "5432:5432"  
       volumes:  
         - pgdata:/var/lib/postgresql/data  

   volumes:  
     pgdata:  

   docker-compose up -d

4. Run the application

   ./gradlew bootRun

   Or build and run the JAR:

   ./gradlew build  
   java -jar build/libs/ecom-backend-*.jar

The app will be available at: http://localhost:8080

---

## 📬 Sample Endpoints

| Method | Endpoint                      | Description                           |
|--------|-------------------------------|---------------------------------------|
| GET    | `/api/products`               | Get paginated product list            |
| POST   | `/api/cart/add`               | Add item to cart                      |
| GET    | `/api/cart`                   | Get current user's cart               |
| DELETE | `/api/cart/remove/{productId}`| Remove item from cart                 |
| POST   | `/api/orders/checkout`        | Checkout current cart                 |
| GET    | `/api/orders`                 | Get user's order history              |
| GET    | `/api/admin/orders`           | Admin: list all orders                |
| PUT    | `/api/admin/orders/status`    | Admin: update order status            |

---

## 📦 Postman Collection

A Postman collection will be provided in the repository to make it easy to test all endpoints.

---

## 📈 Future Improvements

- Add integration/service tests
- Separate `dev`/`prod` profiles
- Product reviews and rating system
- Payment gateway integration
- Email notifications

---

## 📄 License

This project is licensed under the MIT License.  
Use it freely for educational purposes or as a reference for your own backend architecture.
