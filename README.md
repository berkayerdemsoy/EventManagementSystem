<div align="center">

<img src="https://capsule-render.vercel.app/api?type=waving&color=0:0d1117,50:161b22,100:1f6feb&height=220&section=header&text=Event%20Management%20System&fontSize=42&fontColor=ffffff&animation=fadeIn&fontAlignY=35&desc=Production-Ready%20Microservices%20Architecture&descSize=16&descAlignY=55&descAlign=50" width="100%"/>

<br/>

<img src="https://readme-typing-svg.herokuapp.com?font=JetBrains+Mono&weight=600&size=28&duration=3000&pause=1000&color=58A6FF&center=true&vCenter=true&multiline=true&repeat=true&width=700&height=100&lines=Microservices+%7C+Spring+Boot+4;JWT+Security+%7C+Docker+%7C+Cloud+Native;Built+for+Production+%E2%80%94+Designed+for+Scale" alt="Typing SVG" />

<br/>

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.1.1-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-Auth-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![GitHub Packages](https://img.shields.io/badge/GitHub-Packages-181717?style=for-the-badge&logo=github&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.9.9-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

<br/>

![License](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)
![Build](https://img.shields.io/badge/Build-Passing-brightgreen?style=flat-square)
![Status](https://img.shields.io/badge/Status-In%20Development-blue?style=flat-square)

</div>

---

<details>
<summary><strong>TABLE OF CONTENTS</strong></summary>

- [Overview](#overview)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Modules In-Depth](#modules-in-depth)
- [Key Engineering Decisions](#key-engineering-decisions)
- [Getting Started](#getting-started)
- [API Reference](#api-reference)
- [Roadmap](#roadmap)
- [Turkce / Turkish](#turkce)

</details>

---

# Overview

**Event Management System** is a production-grade microservices platform built with **Spring Boot 4** and **Spring Cloud 2025**. The system is designed from the ground up following distributed architecture principles — featuring service discovery, an API gateway, custom JWT-based stateless authentication, a shared common library distributed via **GitHub Packages**, email verification with SHA-256 hashed tokens, role-based access control, and full Docker containerization with health checks.

This is not just a CRUD application; it is an **architectural showcase** demonstrating how to build, structure, and deploy a real-world microservices ecosystem that is ready for cloud-native environments.

---

# Architecture

```
                            +---------------------+
                            |     Client Apps      |
                            | (Web / Mobile / CLI) |
                            +----------+----------+
                                       |
                                       | HTTP (REST)
                                       v
                            +---------------------+
                            |    API Gateway       |
                            |  (Spring Cloud GW)   |
                            |    Port: 8090        |
                            +----------+----------+
                                       |
                           +-----------+-----------+
                           |   Eureka Discovery    |
                           |     Port: 8761        |
                           +-----------+-----------+
                                       |
                          Load Balanced | (lb://)
                                       |
                            +----------+----------+
                            |    User Service      |
                            |    Port: 8080        |
                            +----------+----------+
                                       |
                            +----------+----------+
                            |   PostgreSQL 17      |
                            |    Port: 5432        |
                            +---------------------+
```

```
+-------------------------------------------------------------------+
|                     SHARED LIBRARIES                               |
|                                                                    |
|  +-----------------------+    +-----------------------------+     |
|  |     ems-common        |    |   user-service-client       |     |
|  | (GitHub Packages)     |    |  (Local Maven Module)       |     |
|  |                       |    |                             |     |
|  | - GlobalExceptionHdl  |    | - DTOs & Enums              |     |
|  | - Custom Exceptions   |    | - Declarative HTTP Client   |     |
|  | - ErrorResponseDto    |    | - RestClient + Proxy        |     |
|  | - Auth Interceptor    |    | - Jakarta Validation        |     |
|  | - Auto-Configuration  |    | - PageResponse<T>           |     |
|  +-----------------------+    +-----------------------------+     |
+-------------------------------------------------------------------+
```

---

# Tech Stack

### Core Framework
| Technology | Version | Purpose |
|:---|:---|:---|
| ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white) | **4.0.5** | Application framework |
| ![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-6DB33F?style=flat-square&logo=spring&logoColor=white) | **2025.1.1** | Microservices infrastructure |
| ![Java](https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=openjdk&logoColor=white) | **21 (LTS)** | Programming language |

### Data & Persistence
| Technology | Purpose |
|:---|:---|
| ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=flat-square&logo=postgresql&logoColor=white) | Primary relational database |
| ![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=flat-square&logo=spring&logoColor=white) | ORM & repository abstraction |
| ![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=flat-square&logo=hibernate&logoColor=white) | JPA implementation |

### Security & Authentication
| Technology | Purpose |
|:---|:---|
| ![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=flat-square&logo=springsecurity&logoColor=white) | Security framework |
| ![JWT](https://img.shields.io/badge/JWT-000000?style=flat-square&logo=jsonwebtokens&logoColor=white) | Stateless token authentication (JJWT 0.13.0) |
| ![BCrypt](https://img.shields.io/badge/BCrypt-Hashing-grey?style=flat-square) | Password hashing |
| ![SHA-256](https://img.shields.io/badge/SHA--256-Token%20Hash-grey?style=flat-square) | Email verification token hashing |

### Infrastructure & DevOps
| Technology | Purpose |
|:---|:---|
| ![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white) | Multi-stage containerization |
| ![Docker Compose](https://img.shields.io/badge/Docker%20Compose-2496ED?style=flat-square&logo=docker&logoColor=white) | Service orchestration with health checks |
| ![Netflix Eureka](https://img.shields.io/badge/Netflix%20Eureka-E50914?style=flat-square&logo=netflix&logoColor=white) | Service discovery & registry |
| ![Spring Cloud Gateway](https://img.shields.io/badge/Spring%20Cloud%20Gateway-6DB33F?style=flat-square&logo=spring&logoColor=white) | API Gateway with load balancing |

### Developer Tooling
| Technology | Purpose |
|:---|:---|
| ![MapStruct](https://img.shields.io/badge/MapStruct-1.6.3-red?style=flat-square) | Compile-time object mapping |
| ![Lombok](https://img.shields.io/badge/Lombok-grey?style=flat-square) | Boilerplate reduction |
| ![GitHub Packages](https://img.shields.io/badge/GitHub%20Packages-181717?style=flat-square&logo=github&logoColor=white) | Maven artifact distribution |
| ![Spring Actuator](https://img.shields.io/badge/Actuator-Health%20Monitoring-6DB33F?style=flat-square&logo=spring&logoColor=white) | Production monitoring endpoints |
| ![Resend](https://img.shields.io/badge/Resend-SMTP-000000?style=flat-square) | Transactional email delivery |

---

# Project Structure

```
EventManagementSystem/
|
+-- api-gateway/                    # API Gateway Service
|   +-- Dockerfile                  # Multi-stage Docker build
|   +-- src/main/
|       +-- java/.../ApiGatewayApplication.java
|       +-- resources/application.yaml  # Route definitions, CORS, Eureka config
|
+-- EurekaServer/                   # Service Discovery Server
|   +-- Dockerfile
|   +-- src/main/
|       +-- resources/application.yml   # Standalone Eureka config
|
+-- UserService/                    # User Microservice (Multi-Module)
|   +-- Dockerfile                  # Builds both client & app modules
|   +-- user-service-client/        # Shared Client Library
|   |   +-- pom.xml
|   |   +-- src/main/java/.../
|   |       +-- client/
|   |       |   +-- UserServiceClient.java         # Declarative HTTP interface
|   |       |   +-- UserServiceClientConfig.java    # RestClient + ProxyFactory
|   |       +-- dto/
|   |       |   +-- UserCreateDto.java      # Jakarta Validation
|   |       |   +-- UserResponseDto.java
|   |       |   +-- UserLoginDto.java
|   |       |   +-- UserUpdateDto.java
|   |       |   +-- AuthResponseDto.java
|   |       |   +-- PageResponse.java       # Generic pagination wrapper
|   |       +-- enums/
|   |           +-- Roles.java              # ADMIN, USER, EVENT_OWNER
|   |
|   +-- user-service-app/           # Core Application
|       +-- pom.xml                 # GitHub Packages repo reference
|       +-- src/main/java/.../
|           +-- configs/
|           |   +-- security/
|           |   |   +-- JwtUtil.java        # HMAC-SHA token generation/validation
|           |   |   +-- JwtAuthFilter.java  # OncePerRequestFilter (stateless)
|           |   |   +-- SecurityConfig.java # Filter chain, RBAC rules
|           |   +-- adminLoginConfigs/
|           |   |   +-- AdminProperties.java    # @ConfigurationProperties
|           |   +-- emailConfigs/
|           |       +-- HashUtil.java           # SHA-256 token hashing
|           |       +-- VerificationToken.java  # Token entity (24h expiry)
|           |       +-- VerificationTokenRepository.java
|           +-- controller/
|           |   +-- UserController.java     # REST endpoints
|           +-- entity/
|           |   +-- User.java               # User entity with profile relation
|           |   +-- UserProfile.java        # One-to-one profile entity
|           +-- mapper/
|           |   +-- UserMapper.java         # MapStruct with null-safe updates
|           +-- repository/
|           |   +-- UserRepository.java     # JPA repository with custom queries
|           +-- service/
|           |   +-- UserService.java        # Service interface
|           |   +-- EmailService.java       # Email service interface
|           +-- serviceImpl/
|               +-- UserServiceImpl.java    # Business logic implementation
|               +-- EmailServiceImpl.java   # Resend SMTP integration
|
+-- ems-common/                     # Shared Common Library (GitHub Packages)
|   +-- pom.xml                     # distributionManagement -> GitHub Packages
|   +-- src/main/java/.../
|   |   +-- config/
|   |   |   +-- CommonAutoConfiguration.java       # Auto-imports GlobalExceptionHandler
|   |   |   +-- InterceptorAutoConfiguration.java  # Conditional bean registration
|   |   +-- dto/
|   |   |   +-- ErrorResponseDto.java              # Standardized error format
|   |   +-- exceptions/
|   |   |   +-- GlobalExceptionHandler.java        # @RestControllerAdvice
|   |   |   +-- NotFoundException.java
|   |   |   +-- AlreadyExistsException.java
|   |   |   +-- InvalidCredentialsException.java
|   |   |   +-- ForbiddenException.java
|   |   +-- interceptor/
|   |       +-- AuthRequestInterceptor.java    # JWT propagation for inter-service calls
|   +-- src/main/resources/META-INF/spring/
|       +-- ...AutoConfiguration.imports       # Spring Boot SPI auto-config registration
|
+-- docker-compose.yml              # Full orchestration (4 services + DB)
+-- .env                            # Environment variables (git-ignored)
+-- .gitignore                      # Comprehensive exclusion rules
+-- .dockerignore                   # Docker build context optimization
+-- api-tests.http                  # IntelliJ HTTP Client test file
```

---

# Modules In-Depth

## `ems-common` — Shared Library via GitHub Packages

<img src="https://img.shields.io/badge/Published%20on-GitHub%20Packages-181717?style=for-the-badge&logo=github&logoColor=white" />

This module is the **backbone of cross-cutting concerns** across all microservices. It is published as a Maven artifact to **GitHub Packages** and consumed by other services as a versioned dependency.

**Why this matters:**
- Eliminates code duplication across microservices
- Centralized exception handling — every service gets the same error response format by just adding the dependency
- Spring Boot **Auto-Configuration SPI** — beans are registered automatically, zero manual config in consumer services
- **`@ConditionalOnClass`** ensures the `AuthRequestInterceptor` is only activated in servlet-based services (not in the reactive gateway)

```xml
<!-- Consumer service just adds this dependency -->
<dependency>
    <groupId>com.example</groupId>
    <artifactId>ems-common</artifactId>
    <version>1.0.6</version>
</dependency>
```

**Auto-Configuration Registration** (`META-INF/spring/...AutoConfiguration.imports`):
```
com.example.ems_common.config.CommonAutoConfiguration
com.example.ems_common.config.InterceptorAutoConfiguration
```

## `user-service-client` — Declarative HTTP Interface

This module provides a **type-safe, declarative HTTP client** for other microservices to communicate with the User Service. Built with Spring 6's `HttpServiceProxyFactory` and `RestClient`.

```java
@HttpExchange("/users")
public interface UserServiceClient {
    @GetExchange("/id/{id}")
    UserResponseDto getUserById(@PathVariable("id") Long id);

    @PostExchange("/login")
    AuthResponseDto login(@RequestBody UserLoginDto dto);
    // ...
}
```

**Key Design:**
- Other services just inject `UserServiceClient` — no manual HTTP calls
- `AuthRequestInterceptor` from `ems-common` automatically propagates JWT tokens to outgoing requests
- DTOs with Jakarta Validation constraints are shared, ensuring contract consistency

## `user-service-app` — Core Business Logic

The main application service handling user management, authentication, and email verification.

**Security Architecture (Custom JWT — No UserDetailsService):**
```
Request → JwtAuthFilter → Extract Token → Validate → Set SecurityContext → Controller
```
- Completely **stateless** — no sessions, no database lookup on every request
- JWT contains `userId`, `username`, and `role` as claims
- `JwtAuthFilter` extends `OncePerRequestFilter` and sets `UsernamePasswordAuthenticationToken` directly
- Role-based endpoint protection via `SecurityFilterChain`

**Email Verification Flow:**
```
1. User registers → account created (isVerified=false)
2. POST /verify-email/{id} → generates UUID token
3. Token is SHA-256 hashed before DB storage (never stored in plain text)
4. Verification email sent via Resend SMTP
5. GET /confirm-email?token=... → hash comparison → account verified
6. Token auto-expires after 24 hours
```

**Admin Seeding via Environment Variables:**
```yaml
admin:
  usernames: ${ADMIN_USERNAMES:}  # admin,admin2,admin3
  passwords: ${ADMIN_PASSWORDS:}  # matched by index
```
Admins are automatically detected during registration by matching username/password pairs from environment configuration — no hardcoded values.

---

# Key Engineering Decisions

### 1. GitHub Packages for Shared Libraries
Instead of local `mvn install` or monorepo setups, `ems-common` is published to GitHub Packages as a **versioned Maven artifact**. This mirrors real-world enterprise workflows where shared libraries have their own release lifecycle.

### 2. Multi-Module Service Pattern
The User Service is split into `user-service-client` (DTOs + HTTP interface) and `user-service-app` (implementation). Other microservices only depend on the lightweight client module — they never need the full service code.

### 3. Spring Boot Auto-Configuration SPI
The `ems-common` library uses `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` to register beans automatically. Consumer services get global exception handling and auth interceptors without any `@Import` or `@ComponentScan`.

### 4. Conditional Bean Registration
```java
@AutoConfiguration
@ConditionalOnClass(name = "jakarta.servlet.http.HttpServletRequest")
public class InterceptorAutoConfiguration { ... }
```
This prevents the servlet-based `AuthRequestInterceptor` from being loaded in reactive services (like Spring Cloud Gateway with WebFlux).

### 5. Multi-Stage Docker Builds
Each Dockerfile uses a two-stage approach: build with `maven:3.9.9-eclipse-temurin-21`, run with `eclipse-temurin:21-jre`. This dramatically reduces final image size and eliminates build tools from production.

### 6. Docker Compose Health Checks
Services start in dependency order using `condition: service_healthy`. Eureka must be healthy before the gateway or user service starts. PostgreSQL must pass `pg_isready` before the user service connects.

### 7. Token Hashing for Email Verification
Verification tokens are **never stored in plain text**. The UUID token is hashed with SHA-256 before persistence. On confirmation, the incoming token is hashed and compared — even if the database is compromised, tokens cannot be reversed.

### 8. Environment-Based Configuration
All secrets (JWT secret, DB credentials, API keys, admin passwords) are injected via `.env` files that are **git-ignored**. The application uses Spring's `optional:file:.env[.properties]` import for seamless local development.

---

# Getting Started

### Prerequisites

| Requirement | Version |
|:---|:---|
| Java | 21+ |
| Maven | 3.9+ |
| Docker & Docker Compose | Latest |
| GitHub Account | For GitHub Packages access |

### 1. Clone the Repository

```bash
git clone https://github.com/berkayerdemsoy/EventManagementSystem.git
cd EventManagementSystem
```

### 2. Configure Environment Variables

Create a `.env` file in the project root:

```env
# GitHub Packages Authentication
GITHUB_ACTOR=your_github_username
GITHUB_TOKEN=your_github_personal_access_token

# Admin Seeding
ADMIN_USERNAMES=admin
ADMIN_PASSWORDS=your_secure_password

# Email Service (Resend)
RESEND_API_KEY=your_resend_api_key

# JWT Configuration
JWT_SECRET=your_64_char_hex_secret_key
JWT_EXPIRATION=3600000
```

### 3. Configure Maven for GitHub Packages

Add to your `~/.m2/settings.xml`:

```xml
<settings>
  <servers>
    <server>
      <id>github</id>
      <username>YOUR_GITHUB_USERNAME</username>
      <password>YOUR_GITHUB_TOKEN</password>
    </server>
  </servers>
</settings>
```

### 4. Run with Docker Compose

```bash
docker compose up --build -d
```

This will start:
- **Eureka Server** on `http://localhost:8761`
- **API Gateway** on `http://localhost:8090`
- **User Service** on internal port `8080` (accessible via gateway)
- **PostgreSQL** on `localhost:5432`

### 5. Local Development (Without Docker)

```bash
# Start PostgreSQL locally or via Docker
docker run -d --name user-db -p 5432:5432 \
  -e POSTGRES_DB=user_service_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  postgres:17-alpine

# Build shared modules
cd ems-common && mvn clean install -DskipTests
cd ../UserService/user-service-client && mvn clean install -DskipTests

# Run services
cd ../../EurekaServer && mvn spring-boot:run
cd ../api-gateway && mvn spring-boot:run
cd ../UserService/user-service-app && mvn spring-boot:run
```

---

# API Reference

All requests go through the **API Gateway** at `http://localhost:8090`.

### Public Endpoints (No Auth Required)

| Method | Endpoint | Description |
|:---|:---|:---|
| `POST` | `/users/create` | Register a new user |
| `POST` | `/users/login` | Authenticate and receive JWT |
| `GET` | `/users/confirm-email?token=...` | Confirm email verification |

### Authenticated Endpoints (Bearer Token Required)

| Method | Endpoint | Description |
|:---|:---|:---|
| `GET` | `/users/id/{id}` | Get user by ID |
| `GET` | `/users/username/{username}` | Get user by username |
| `POST` | `/users/update/{id}` | Update user profile |
| `POST` | `/users/owner/{id}` | Upgrade to EVENT_OWNER role |
| `POST` | `/users/verify-email/{id}` | Send verification email |

### Admin-Only Endpoints (ADMIN Role Required)

| Method | Endpoint | Description |
|:---|:---|:---|
| `GET` | `/users/all?page=0&size=20` | List all users (paginated) |
| `DELETE` | `/users/{id}` | Delete a user |

### Example Request

```http
POST http://localhost:8090/users/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password"
}
```

### Example Response

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": 1,
    "username": "admin",
    "email": "admin@gmail.com",
    "role": "ADMIN",
    "verified": true,
    "createdAt": "2025-04-01T12:00:00"
  }
}
```

---

# Roadmap

<img src="https://readme-typing-svg.herokuapp.com?font=JetBrains+Mono&weight=500&size=20&duration=3000&pause=1000&color=58A6FF&center=true&vCenter=true&width=600&lines=Building+towards+a+full-stack+cloud-native+platform" alt="Roadmap" />

### Phase 1 — Current (Backend Microservices) &check;
- [x] Microservices architecture with Spring Boot 4
- [x] Netflix Eureka service discovery
- [x] Spring Cloud Gateway with load balancing
- [x] Custom JWT authentication (stateless, no UserDetailsService)
- [x] Role-based access control (ADMIN, USER, EVENT_OWNER)
- [x] Email verification with hashed tokens
- [x] Shared library via GitHub Packages (`ems-common`)
- [x] Declarative HTTP client pattern (`user-service-client`)
- [x] Docker Compose orchestration with health checks
- [x] Multi-stage Docker builds
- [x] Environment-based secret management

### Phase 2 — Event & Ticket Services (Next)
- [ ] Event Service (CRUD, search, categories)
- [ ] Ticket Service (purchase, QR code generation)
- [ ] Payment Service integration
- [ ] Inter-service communication via RestClient
- [ ] Distributed tracing with Micrometer + Zipkin

### Phase 3 — Frontend
- [ ] Angular / React frontend application
- [ ] Full-stack integration through API Gateway
- [ ] Responsive UI with event browsing, booking, and user dashboard

### Phase 4 — AWS Cloud Deployment
- [ ] AWS ECS / EKS container orchestration
- [ ] AWS RDS for managed PostgreSQL
- [ ] AWS Application Load Balancer (ALB) configuration
- [ ] AWS ECR for container registry
- [ ] CI/CD pipeline with GitHub Actions
- [ ] Infrastructure as Code (Terraform / CloudFormation)

### Phase 5 — Production Hardening
- [ ] Security vulnerability scanning (OWASP, Trivy, Snyk)
- [ ] Penetration testing — local & production
- [ ] Rate limiting and DDoS protection (AWS WAF)
- [ ] Centralized logging (ELK Stack / CloudWatch)
- [ ] Performance testing with JMeter / Gatling
- [ ] SSL/TLS termination at load balancer
- [ ] Database migration with Flyway/Liquibase

---

<div align="center">

<img src="https://capsule-render.vercel.app/api?type=rect&color=0:0d1117,100:161b22&height=2&section=header" width="100%"/>

</div>

---

<a name="turkce"></a>

<div align="center">

<img src="https://capsule-render.vercel.app/api?type=soft&color=0d1117&height=80&section=header&text=T%C3%BCrk%C3%A7e%20Dok%C3%BCmantasyon&fontSize=30&fontColor=58A6FF&animation=fadeIn" width="100%"/>

</div>

# Genel Bakis

**Event Management System**, **Spring Boot 4** ve **Spring Cloud 2025** ile sifirdan insa edilmis, uretim ortamina hazir bir mikroservis platformudur. Sistem; servis kesfinden (Eureka), API Gateway'den, ozel JWT tabanli durumsuz (stateless) kimlik dogrulamadan, **GitHub Packages** uzerinden dagitilan ortak kutuphane modulunden, SHA-256 ile hashlenmis e-posta dogrulama tokenlarindan, rol tabanli erisim kontrolunden ve saglik kontrolleri iceren tam Docker konteynerizasyonundan olusmaktadir.

Bu yalnizca bir CRUD uygulamasi degildir; gercek dunya mikroservis ekosisteminin nasil insa edilecegini, yapilandirilacagini ve deploy edilecegini gosteren **mimari bir vitrindir**.

---

# Mimari Yaklasim

```
                            +---------------------+
                            |   Istemci (Client)   |
                            | (Web / Mobil / CLI)  |
                            +----------+----------+
                                       |
                                       v
                            +---------------------+
                            |    API Gateway       |
                            |  (Spring Cloud GW)   |
                            |    Port: 8090        |
                            +----------+----------+
                                       |
                           +-----------+-----------+
                           |   Eureka Discovery    |
                           |     Port: 8761        |
                           +-----------+-----------+
                                       |
                          Yuk Dengeleme | (lb://)
                                       |
                            +----------+----------+
                            |    User Service      |
                            |    Port: 8080        |
                            +----------+----------+
                                       |
                            +----------+----------+
                            |   PostgreSQL 17      |
                            |    Port: 5432        |
                            +---------------------+
```

---

# Kullanilan Teknolojiler

| Kategori | Teknoloji | Aciklama |
|:---|:---|:---|
| **Framework** | Spring Boot 4.0.5 + Spring Cloud 2025.1.1 | Temel uygulama cercevesi |
| **Dil** | Java 21 (LTS) | Programlama dili |
| **Veritabani** | PostgreSQL 17 + Spring Data JPA | Veri katmani |
| **Guvenlik** | Spring Security + Custom JWT (JJWT 0.13.0) | Kimlik dogrulama ve yetkilendirme |
| **Sifreleme** | BCrypt (sifre) + SHA-256 (token) | Hassas veri koruma |
| **API Gateway** | Spring Cloud Gateway WebMVC | Yonlendirme, yuk dengeleme, CORS |
| **Servis Kesfi** | Netflix Eureka | Dinamik servis kayit ve kesif |
| **Nesne Esleme** | MapStruct 1.6.3 | Derleme zamanli DTO-Entity donusumu |
| **E-posta** | Spring Mail + Resend SMTP | Islemsel e-posta gonderimi |
| **Konteyner** | Docker + Docker Compose | Cok asamali build ve orkestrasyon |
| **Paket Yonetimi** | GitHub Packages | Maven artifact dagitimi |
| **Izleme** | Spring Actuator | Saglik kontrolu ve metrikler |

---

# Onemli Muhendislik Kararlari ve Teknik Detaylar

### 1. GitHub Packages ile Paylasimli Kutuphane (`ems-common`)
`ems-common` modulu, tum mikroservislerin ortaklasa kullandigi exception handling, error response formati ve auth interceptor icerir. Bu modul **GitHub Packages** uzerinde Maven artifact olarak yayinlanir. Tuketici servisler sadece dependency ekleyerek tum bu yetenekleri otomatik olarak kazanir.

**Spring Boot Auto-Configuration SPI** sayesinde hicbir `@Import` veya `@ComponentScan` yapilmadan bean'ler otomatik register olur.

### 2. Cok Modullu Servis Deseni (Client-App Ayirimi)
User Service, iki alt module ayrilmistir:
- **`user-service-client`**: DTO'lar, enum'lar ve deklaratif HTTP istemcisi
- **`user-service-app`**: Is mantigi, entity'ler, guvenlik yapilandirmasi

Diger mikroservisler yalnizca hafif `client` modulune bagimlidir — tam servis koduna erismeleri gerekmez.

### 3. Ozel JWT Kimlik Dogrulama (UserDetailsService Olmadan)
Sistem, veritabanina her istekte sorgu atan klasik `UserDetailsService` yaklasimi yerine tamamen **durumsuz (stateless)** bir JWT cozumu kullanir:
- Token icinde `userId`, `username` ve `role` claim'leri bulunur
- `JwtAuthFilter`, her istekte tokeni dogrular ve `SecurityContext`'e authentication nesnesi yerlestirir
- Veritabanina hicbir ek sorgu atilmaz

### 4. SHA-256 ile Token Hashleme
E-posta dogrulama tokenlari **asla duz metin olarak** veritabaninda saklanmaz. UUID token olusturulur, SHA-256 ile hashlendikten sonra veritabanina yazilir. Dogrulama sirasinda gelen token ayni sekilde hashlenir ve karsilastirilir.

### 5. Ortam Degiskeni Tabanli Konfigrasyon
Tum hassas bilgiler (JWT secret, DB sifresi, API anahtarlari, admin bilgileri) `.env` dosyalari uzerinden enjekte edilir. Bu dosyalar **`.gitignore`** ile versiyon kontrolunden dislanir. Spring'in `optional:file:.env[.properties]` ozelligi ile yerel gelistirmede sorunsuz calisir.

### 6. Kosullu Bean Kaydı (`@ConditionalOnClass`)
```java
@ConditionalOnClass(name = "jakarta.servlet.http.HttpServletRequest")
```
Bu yaklasim, servlet tabanli `AuthRequestInterceptor`'un yalnizca uygun servislerde (WebMVC) yuklenmesini saglar. Reaktif servisler (WebFlux tabanli Gateway) etkilenmez.

### 7. Cok Asamali Docker Build
Her Dockerfile iki asamadan olusur:
1. **Build asamasi**: `maven:3.9.9-eclipse-temurin-21` ile derleme
2. **Run asamasi**: `eclipse-temurin:21-jre` ile calistirma

Bu yaklasim final image boyutunu onemli olcude kucultir ve uretim ortamindan build araclarini temizler.

### 8. Docker Compose Saglik Kontrolleri
Servisler bagimlilik sirasina gore baslar:
- PostgreSQL `pg_isready` komutunu gecmeden User Service baslamaz
- Eureka `actuator/health` yanit vermeden Gateway ve servisler baslamaz

---

# Kurulum ve Calistirma

### Gereksinimler

| Gereksinim | Surum |
|:---|:---|
| Java | 21+ |
| Maven | 3.9+ |
| Docker & Docker Compose | Guncel |
| GitHub Hesabi | GitHub Packages erisimi icin |

### Hizli Baslangic (Docker ile)

```bash
# 1. Repoyu klonlayin
git clone https://github.com/berkayerdemsoy/EventManagementSystem.git
cd EventManagementSystem

# 2. .env dosyasini yapilandirin (yukaridaki ornege bakin)

# 3. Tum servisleri baslatin
docker compose up --build -d
```

### Servis Adresleri

| Servis | Adres |
|:---|:---|
| API Gateway | `http://localhost:8090` |
| Eureka Dashboard | `http://localhost:8761` |
| PostgreSQL | `localhost:5432` |

---

# Gelecek Vizyonu

### Faz 1 — Mevcut Durum (Backend Mikroservisler) &check;
- [x] Spring Boot 4 ile mikroservis mimarisi
- [x] Netflix Eureka servis kesfi
- [x] Spring Cloud Gateway ile yuk dengeleme
- [x] Ozel JWT kimlik dogrulama (stateless)
- [x] Rol tabanli erisim kontrolu (ADMIN, USER, EVENT_OWNER)
- [x] Hashlenmis tokenlarla e-posta dogrulama
- [x] GitHub Packages ile paylasimli kutuphane
- [x] Deklaratif HTTP istemci deseni
- [x] Docker Compose orkestrasyon

### Faz 2 — Etkinlik ve Bilet Servisleri
- [ ] Event Service (CRUD, arama, kategoriler)
- [ ] Ticket Service (satin alma, QR kod)
- [ ] Odeme servisi entegrasyonu
- [ ] Micrometer + Zipkin ile dagitik izleme

### Faz 3 — Frontend
- [ ] Angular / React frontend uygulamasi
- [ ] API Gateway uzerinden full-stack entegrasyon
- [ ] Duyarli (responsive) kullanici arayuzu

### Faz 4 — AWS Cloud Deployment
- [ ] AWS ECS / EKS konteyner orkestrasyonu
- [ ] AWS RDS yonetilen veritabani
- [ ] AWS Application Load Balancer (ALB)
- [ ] GitHub Actions ile CI/CD pipeline
- [ ] Terraform / CloudFormation ile IaC

### Faz 5 — Uretim Saglamiligi
- [ ] Guvenlik zafiyet taramasi (OWASP, Trivy, Snyk)
- [ ] Penetrasyon testi — yerel ve canlı ortam
- [ ] Hiz sinirlandirma ve DDoS koruması (AWS WAF)
- [ ] Merkezi log yonetimi (ELK Stack / CloudWatch)
- [ ] Performans testi (JMeter / Gatling)
- [ ] SSL/TLS sonlandirma
- [ ] Flyway/Liquibase ile veritabani migrasyonu

---

<div align="center">

<img src="https://capsule-render.vercel.app/api?type=waving&color=0:0d1117,50:161b22,100:1f6feb&height=120&section=footer&animation=fadeIn" width="100%"/>

**Built with determination and passion for distributed systems.**

![Profile Views](https://komarev.com/ghpvc/?username=berkayerdemsoy&color=58A6FF&style=flat-square&label=Profile+Views)

</div>

