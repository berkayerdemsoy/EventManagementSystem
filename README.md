<div align="center">

<img src="https://capsule-render.vercel.app/api?type=waving&color=0:0d1117,50:161b22,100:1f6feb&height=220&section=header&text=Event%20Management%20System&fontSize=42&fontColor=ffffff&animation=fadeIn&fontAlignY=35&desc=Production-Ready%20Microservices%20Architecture&descSize=16&descAlignY=55&descAlign=50" width="100%"/>

<br/>

<img src="https://readme-typing-svg.herokuapp.com?font=JetBrains+Mono&weight=600&size=28&duration=3000&pause=1000&color=58A6FF&center=true&vCenter=true&multiline=true&repeat=true&width=700&height=100&lines=Microservices+%7C+Spring+Boot+4;JWT+Security+%7C+Kafka+%7C+Docker+%7C+Cloud+Native;Built+for+Production+%E2%80%94+Designed+for+Scale" alt="Typing SVG" />

<br/>

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.1.1-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-3.7.0-231F20?style=for-the-badge&logo=apachekafka&logoColor=white)
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

**Event Management System** is a production-grade microservices platform built with **Spring Boot 4** and **Spring Cloud 2025**. The system is designed from the ground up following distributed architecture principles — featuring service discovery, an API gateway, custom JWT-based stateless authentication, a shared common library distributed via **GitHub Packages**, email verification with SHA-256 hashed tokens, role-based access control, **Apache Kafka event-driven async notifications**, **Quartz distributed scheduling**, and full Docker containerization with health checks.

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
                  +--------------------+--------------------+
                  |                                         |
       +----------+----------+               +-------------+----------+
       |    User Service      |               |    Event Service       |
       |    Port: 8080        |               |    Port: 8081          |
       +----------+----------+               +-------------+----------+
                  |                                         |
       +----------+----------+               +-------------+----------+
       |   PostgreSQL 17      |               |   PostgreSQL 17        |
       |  user_service_db     |               |  event_service_db      |
       |    Port: 5432        |               |    Port: 5433          |
       +---------------------+               +-----------------------+
                  |                                         |
                  +-------------------+---------------------+
                                      |
                                      | Kafka Producer
                                      v
                            +---------------------+
                            |  Apache Kafka 3.7.0  |
                            |  (KRaft — ZK yok)    |
                            +----------+----------+
                                       |
                                       | Kafka Consumer
                                       v
                            +---------------------+
                            | Notification Service |
                            |  (Strategy Pattern)  |
                            +----------+----------+
                                       |
                                       v
                            +---------------------+
                            |   Resend SMTP        |
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
|  | - NotificationEvent   |    | - PageResponse<T>           |     |
|  | - NotificationEvType  |    |                             |     |
|  | - Auto-Configuration  |    +-----------------------------+     |
|  +-----------------------+                                        |
|                                                                    |
|                          +-----------------------------+           |
|                          |   event-service-client      |           |
|                          |  (Local Maven Module)       |           |
|                          |                             |           |
|                          | - DTOs & Enums              |           |
|                          | - Declarative HTTP Client   |           |
|                          | - EventStatus enum          |           |
|                          | - CategoryDto               |           |
|                          +-----------------------------+           |
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
| ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=flat-square&logo=postgresql&logoColor=white) | Primary relational database (per-service isolation) |
| ![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=flat-square&logo=spring&logoColor=white) | ORM & repository abstraction |
| ![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=flat-square&logo=hibernate&logoColor=white) | JPA implementation |

### Messaging & Async
| Technology | Version | Purpose |
|:---|:---|:---|
| ![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-231F20?style=flat-square&logo=apachekafka&logoColor=white) | **3.7.0 (KRaft)** | Async event-driven notifications — no ZooKeeper |
| ![Kafka UI](https://img.shields.io/badge/Kafka%20UI-provectuslabs-blue?style=flat-square) | latest | Topic & DLQ monitoring dashboard (port 8085) |
| ![Quartz](https://img.shields.io/badge/Quartz-Scheduler-red?style=flat-square) | Spring Boot Starter | Distributed cron scheduling via JdbcJobStore |

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
| ![Docker Compose](https://img.shields.io/badge/Docker%20Compose-2496ED?style=flat-square&logo=docker&logoColor=white) | Service orchestration with health checks (8 services) |
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
|           +-- kafka/
|           |   +-- NotificationEventProducer.java  # Async notification producer
|           +-- mapper/
|           |   +-- UserMapper.java         # MapStruct with null-safe updates
|           +-- repository/
|           |   +-- UserRepository.java     # JPA repository with custom queries
|           +-- service/
|           |   +-- UserService.java        # Service interface
|           |   +-- EmailService.java       # Email service interface (fallback)
|           +-- serviceImpl/
|               +-- UserServiceImpl.java    # Business logic implementation
|               +-- EmailServiceImpl.java   # Resend SMTP (direct fallback)
|
+-- event-service/                  # Event Microservice (Multi-Module)
|   +-- Dockerfile                  # Builds both client & app from project root
|   +-- event-service-client/       # Shared Client Library
|   |   +-- pom.xml
|   |   +-- src/main/java/.../
|   |       +-- client/
|   |       |   +-- EventServiceClient.java         # Declarative HTTP interface
|   |       |   +-- EventServiceClientConfig.java    # RestClient + ProxyFactory
|   |       +-- dto/
|   |       |   +-- EventCreateDto.java
|   |       |   +-- EventResponseDto.java
|   |       |   +-- EventUpdateDto.java
|   |       |   +-- CategoryDto.java
|   |       |   +-- ParticipationCreateDto.java
|   |       |   +-- ParticipationResponseDto.java
|   |       +-- enums/
|   |           +-- EventStatus.java        # UPCOMING, ONGOING, COMPLETED, CANCELLED
|   |
|   +-- event-service-app/          # Core Application
|       +-- pom.xml
|       +-- src/main/java/.../
|           +-- configs/
|           |   +-- security/
|           |   |   +-- SecurityConfig.java # JWT auth filter chain
|           |   +-- QuartzConfig.java       # Distributed cron job setup
|           +-- controller/
|           |   +-- EventController.java        # REST: CRUD + filter endpoints
|           |   +-- CategoryController.java     # REST: category management
|           |   +-- ParticipationController.java # REST: event registration
|           +-- entity/
|           |   +-- Event.java              # ownerEmail denormalized field
|           |   +-- Category.java
|           |   +-- Participation.java      # participantEmail + reminderSent fields
|           +-- kafka/
|           |   +-- NotificationEventProducer.java  # recipientEmail partition key
|           +-- mapper/
|           |   +-- EventMapper.java
|           |   +-- CategoryMapper.java
|           +-- repository/
|           |   +-- EventRepository.java
|           |   +-- CategoryRepository.java
|           |   +-- ParticipationRepository.java    # findPendingReminders query
|           +-- scheduler/
|           |   +-- EventReminderJob.java   # QuartzJobBean + @DisallowConcurrentExecution
|           +-- service/ & serviceImpl/
|               +-- EventService(Impl).java
|               +-- CategoryService(Impl).java
|               +-- ParticipationService(Impl).java
|
+-- notification-service/           # Notification Microservice
|   +-- Dockerfile
|   +-- src/main/java/.../
|       +-- config/
|       |   +-- KafkaConfig.java        # DLQ template + error handler + concurrency
|       +-- dto/
|       |   +-- NotificationEvent.java  # (from ems-common)
|       +-- service/
|           +-- ConsumerService.java    # @KafkaListener + EnumMap dispatch
|           +-- handler/
|               +-- NotificationHandler.java          # Strategy interface
|               +-- EmailVerificationHandler.java
|               +-- EventOwnerWelcomeHandler.java
|               +-- ParticipantRegisteredHandler.java
|               +-- EventReminderHandler.java
|
+-- ems-common/                     # Shared Common Library (GitHub Packages)
|   +-- pom.xml                     # distributionManagement -> GitHub Packages
|   +-- src/main/java/.../
|   |   +-- config/
|   |   |   +-- CommonAutoConfiguration.java       # Auto-imports GlobalExceptionHandler
|   |   |   +-- InterceptorAutoConfiguration.java  # Conditional bean registration
|   |   +-- dto/
|   |   |   +-- ErrorResponseDto.java              # Standardized error format
|   |   |   +-- NotificationEvent.java             # Shared Kafka message DTO
|   |   |   +-- NotificationEventType.java         # EMAIL_VERIFICATION, EVENT_OWNER_WELCOME,
|   |   |                                          # PARTICIPANT_REGISTERED, EVENT_REMINDER
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
+-- docker-compose.yml              # Full orchestration (8 services + 2 DBs + Kafka)
+-- .env                            # Environment variables (git-ignored)
+-- .gitignore                      # Comprehensive exclusion rules
+-- .dockerignore                   # Docker build context optimization
+-- api-tests.http                  # IntelliJ HTTP Client test file
+-- kafka.md                        # Kafka architecture deep-dive documentation
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
- **`NotificationEvent` + `NotificationEventType`** are co-located here so all Kafka producers and consumers share the same fully-qualified class name — preventing JSON deserialization type-mismatch errors

```xml
<!-- Consumer service just adds this dependency -->
<dependency>
    <groupId>com.example</groupId>
    <artifactId>ems-common</artifactId>
    <version>1.1.3</version>
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

**Email Verification Flow (Async via Kafka):**
```
1. User registers → account created (isVerified=false)
2. POST /verify-email/{id} → generates UUID token
3. Token is SHA-256 hashed before DB storage (never stored in plain text)
4. NotificationEventProducer.send(EMAIL_VERIFICATION) → Kafka topic
5. NotificationService consumes → EmailVerificationHandler → Resend SMTP
6. GET /confirm-email?token=... → hash comparison → account verified
7. Token auto-expires after 24 hours
```
> Previously: SMTP was called synchronously, blocking the HTTP thread. Now the HTTP response returns immediately; Kafka handles delivery asynchronously with retry + DLQ.

**Admin Seeding via Environment Variables:**
```yaml
admin:
  usernames: ${ADMIN_USERNAMES:}  # admin,admin2,admin3
  passwords: ${ADMIN_PASSWORDS:}  # matched by index
```
Admins are automatically detected during registration by matching username/password pairs from environment configuration — no hardcoded values.

## `event-service` — Event Management (Multi-Module)

The Event Service follows the same **client-app split pattern** as the User Service. It handles event CRUD, category management, participation tracking, and distributed reminders.

### `event-service-client`
```java
@HttpExchange("/events")
public interface EventServiceClient {
    @PostExchange
    EventResponseDto createEvent(@RequestBody EventCreateDto dto);

    @GetExchange("/category/{categoryId}")
    List<EventResponseDto> getEventsByCategory(@PathVariable("categoryId") Long categoryId);

    @GetExchange("/city/{city}")
    List<EventResponseDto> getEventsByCity(@PathVariable("city") String city);
    // ...
}
```

### `event-service-app` — Key Design Decisions

**Denormalized `ownerEmail` on Event entity:**
When an event is created, the owner's email is fetched via `UserServiceClient` (already called for ownership verification) and stored directly on the event row. This eliminates repeated HTTP calls to User Service for every notification or listing operation.

**Denormalized `participantEmail` on Participation entity:**
Participant emails are passed by the client at registration time and persisted directly. When the Quartz reminder job fires, it can build thousands of `NotificationEvent` objects purely from local DB data — no HTTP fan-out to User Service.

**Quartz JdbcJobStore — Horizontal Scale Safe:**
```yaml
spring:
  quartz:
    job-store-type: jdbc          # PostgreSQL-backed, not in-memory
    jdbc:
      initialize-schema: always   # Auto-creates QRTZ_* tables
    properties:
      org.quartz.jobStore.isClustered: true
      org.quartz.scheduler.instanceId: AUTO
      org.quartz.jobStore.driverDelegateClass: org.quartz.impl.jdbcjobstore.PostgreSQLDelegate
      org.quartz.jobStore.useProperties: true
```

```java
@DisallowConcurrentExecution   // No two nodes run the same job simultaneously
@PersistJobDataAfterExecution  // Stateful job — persists data after each run
public class EventReminderJob extends QuartzJobBean { ... }
```

Every hour Quartz acquires a **pessimistic DB lock** (`QRTZ_LOCKS` table). Only one node across the cluster wins the lock and runs the job — preventing duplicate reminder emails even at N replicas.

**Kafka Producer — Partition Key:**
```java
kafkaTemplate.send(topic, event.getRecipientEmail(), event)
//                         ↑ recipientEmail as partition key
```
Messages for the same recipient always land on the same partition — guaranteeing delivery order per user.

## `notification-service` — Event-Driven Email Delivery

A dedicated, stateless consumer service. No database, no REST endpoints — pure Kafka consumer.

**Strategy Pattern + EnumMap Dispatch:**
```java
@Service
public class ConsumerService {
    private final Map<NotificationEventType, NotificationHandler> handlerMap;

    @PostConstruct
    void initHandlerMap() {
        handlers.forEach(h -> handlerMap.put(h.getHandledType(), h));
    }

    @KafkaListener(topics = "${kafka.topics.notification-events:notification-events}")
    public void consume(NotificationEvent event) {
        handlerMap.get(event.getEventType()).handle(event);  // zero if-else
    }
}
```

Adding a new notification type requires:
1. Adding a value to `NotificationEventType` enum in `ems-common`
2. Writing a new `@Component` that implements `NotificationHandler`

Nothing else changes — `ConsumerService`, `KafkaConfig`, producers are all untouched (**Open/Closed Principle**).

**Supported Notification Types:**

| Type | Trigger | Payload |
|:---|:---|:---|
| `EMAIL_VERIFICATION` | `POST /users/verify-email/{id}` | `verificationLink` |
| `EVENT_OWNER_WELCOME` | `POST /events` (event created) | `ownerName`, `eventTitle`, `eventId` |
| `PARTICIPANT_REGISTERED` | `POST /participations` | `eventTitle`, `eventDate`, `eventCity`, `eventId` |
| `EVENT_REMINDER` | Quartz job (hourly) | `eventTitle`, `eventDate`, `eventCity`, `eventId` |

**Retry + Dead Letter Queue (DLQ):**
```java
// KafkaConfig.java
DefaultErrorHandler errorHandler = new DefaultErrorHandler(
    new DeadLetterPublishingRecoverer(
        dlqKafkaTemplate,
        (record, ex) -> new TopicPartition(dlqTopic, 0)
    ),
    new FixedBackOff(2000L, 3)  // 3 retries × 2s interval
);
factory.setConcurrency(3);      // 3 parallel consumer threads
```

A **separate `dlqKafkaTemplate`** bean is used specifically for DLQ publishing — using the main consumer's template would create a circular Spring bean dependency.

Failed messages after 3 retries are routed to `notification-events-dlq` and can be inspected and replayed via the **Kafka UI** dashboard at `http://localhost:8085`.

---

# Key Engineering Decisions

### 1. GitHub Packages for Shared Libraries
Instead of local `mvn install` or monorepo setups, `ems-common` is published to GitHub Packages as a **versioned Maven artifact**. This mirrors real-world enterprise workflows where shared libraries have their own release lifecycle.

### 2. Multi-Module Service Pattern
Both the User Service and Event Service are split into `*-client` (DTOs + HTTP interface) and `*-app` (implementation) modules. Other microservices only depend on the lightweight client module — they never need the full service code.

### 3. Spring Boot Auto-Configuration SPI
The `ems-common` library uses `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` to register beans automatically. Consumer services get global exception handling and auth interceptors without any `@Import` or `@ComponentScan`.

### 4. Conditional Bean Registration
```java
@AutoConfiguration
@ConditionalOnClass(name = "jakarta.servlet.http.HttpServletRequest")
public class InterceptorAutoConfiguration { ... }
```
This prevents the servlet-based `AuthRequestInterceptor` from being loaded in reactive services (like Spring Cloud Gateway with WebFlux).

### 5. Kafka KRaft — No ZooKeeper
Apache Kafka 3.7.0 runs in **KRaft mode** (Kafka Raft Metadata). ZooKeeper is completely removed from the stack, reducing infrastructure footprint and startup time.

```yaml
KAFKA_PROCESS_ROLES: "broker,controller"
KAFKA_LISTENERS: "INTERNAL://:29092,EXTERNAL://:9092,CONTROLLER://:9093"
KAFKA_ADVERTISED_LISTENERS: "INTERNAL://kafka:29092,EXTERNAL://localhost:9092"
KAFKA_CONTROLLER_QUORUM_VOTERS: "1@kafka:9093"
```

Three listener types serve distinct purposes:
- **`INTERNAL`** (port 29092) — inter-container communication within Docker network
- **`EXTERNAL`** (port 9092) — external access from host machine (dev tools, local producers)
- **`CONTROLLER`** (port 9093) — Raft leader election and metadata replication

### 6. JSON Serialization — Type Header Disabled

```yaml
# Producer (user-service, event-service):
spring.json.add.type.headers: false   # Don't embed __TypeId__ header

# Consumer (notification-service):
spring.json.value.default.type: com.example.ems_common.dto.NotificationEvent
```

If type headers were enabled, the consumer would look for `com.example.user_service_app.kafka.NotificationEvent` — a class that doesn't exist in notification-service. By disabling headers and specifying `value.default.type`, the consumer always deserializes to the shared ems-common DTO regardless of which service produced the message.

### 7. Quartz over `@Scheduled` for Distributed Environments
`@Scheduled` fires on **every running instance** simultaneously. With 2+ event-service replicas, the same user would receive duplicate reminder emails.

Quartz `JdbcJobStore` with `isClustered: true` uses a **pessimistic DB lock** (`QRTZ_LOCKS` table) to ensure only one node fires the job. `@DisallowConcurrentExecution` adds an additional guard within a single JVM. The `PostgreSQLDelegate` driver delegate is mandatory — without it, Quartz would attempt to store job data as binary blobs which PostgreSQL rejects.

### 8. Multi-Stage Docker Builds
Each Dockerfile uses a two-stage approach: build with `maven:3.9.9-eclipse-temurin-21`, run with `eclipse-temurin:21-jre`. This dramatically reduces final image size and eliminates build tools from production.

### 9. Docker Compose Health Checks & Startup Order
Services start in strict dependency order using `condition: service_healthy`:
```
PostgreSQL (pg_isready) → User Service, Event Service
Eureka (actuator/health) → API Gateway, User Service, Event Service
Kafka (kafka-topics.sh --list) → Notification Service, User Service, Event Service
```

### 10. Token Hashing for Email Verification
Verification tokens are **never stored in plain text**. The UUID token is hashed with SHA-256 before persistence. On confirmation, the incoming token is hashed and compared — even if the database is compromised, tokens cannot be reversed.

### 11. Environment-Based Configuration
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

This will start **8 containers**:

| Container | Port | Description |
|:---|:---|:---|
| **Eureka Server** | `8761` | Service discovery dashboard |
| **API Gateway** | `8090` | Single entry point for all clients |
| **User Service** | internal `8080` | Auth, user management |
| **Event Service** | internal `8081` | Events, categories, participations |
| **Notification Service** | internal `8083` | Kafka consumer, email delivery |
| **Apache Kafka** | `9092` | Message broker (KRaft mode) |
| **Kafka UI** | `8085` | Topic & DLQ monitoring |
| **PostgreSQL - User** | `5432` | User service database |
| **PostgreSQL - Event** | `5433` | Event service database |

### 5. Local Development (Without Docker)

```bash
# Start PostgreSQL databases
docker run -d --name user-db -p 5432:5432 \
  -e POSTGRES_DB=user_service_db -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres postgres:17-alpine

docker run -d --name event-db -p 5433:5432 \
  -e POSTGRES_DB=event_service_db -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres postgres:17-alpine

# Start Kafka (KRaft)
docker run -d --name kafka -p 9092:9092 \
  -e KAFKA_PROCESS_ROLES=broker,controller \
  -e KAFKA_NODE_ID=1 \
  -e CLUSTER_ID=MkU3OEVBNTcwNTJENDM2Qk \
  apache/kafka:3.7.0

# Build shared modules
cd ems-common && mvn clean install -DskipTests
cd ../UserService/user-service-client && mvn clean install -DskipTests
cd ../../event-service/event-service-client && mvn clean install -DskipTests

# Run services
cd ../../EurekaServer && mvn spring-boot:run
cd ../api-gateway && mvn spring-boot:run
cd ../UserService/user-service-app && mvn spring-boot:run
cd ../../event-service/event-service-app && mvn spring-boot:run
cd ../../notification-service && mvn spring-boot:run
```

---

# API Reference

All requests go through the **API Gateway** at `http://localhost:8090`.

## User Service Endpoints

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
| `POST` | `/users/verify-email/{id}` | Send verification email (async via Kafka) |

### Admin-Only Endpoints (ADMIN Role Required)

| Method | Endpoint | Description |
|:---|:---|:---|
| `GET` | `/users/all?page=0&size=20` | List all users (paginated) |
| `DELETE` | `/users/{id}` | Delete a user |

## Event Service Endpoints

### Event Endpoints (EVENT_OWNER / ADMIN for writes)

| Method | Endpoint | Description |
|:---|:---|:---|
| `POST` | `/events` | Create event → triggers `EVENT_OWNER_WELCOME` notification |
| `GET` | `/events/{id}` | Get event by ID |
| `PUT` | `/events/{id}` | Update event |
| `DELETE` | `/events/{id}` | Delete event |
| `GET` | `/events?page=0&size=20` | List all events (paginated) |
| `GET` | `/events/category/{categoryId}` | Filter events by category |
| `GET` | `/events/city/{city}` | Filter events by city |
| `GET` | `/events/date-range?start=...&end=...` | Filter events by date range |

### Category Endpoints

| Method | Endpoint | Description |
|:---|:---|:---|
| `POST` | `/categories` | Create category (ADMIN) |
| `GET` | `/categories` | List all categories |
| `GET` | `/categories/{id}` | Get category by ID |
| `PUT` | `/categories/{id}` | Update category (ADMIN) |
| `DELETE` | `/categories/{id}` | Delete category (ADMIN) |

### Participation Endpoints

| Method | Endpoint | Description |
|:---|:---|:---|
| `POST` | `/participations` | Register for event → triggers `PARTICIPANT_REGISTERED` notification |
| `GET` | `/participations/{id}` | Get participation by ID |
| `GET` | `/participations/event/{eventId}` | List participants for an event |
| `DELETE` | `/participations/{id}` | Cancel participation |

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

### Phase 1 — Backend Microservices &check;
- [x] Microservices architecture with Spring Boot 4
- [x] Netflix Eureka service discovery
- [x] Spring Cloud Gateway with load balancing
- [x] Custom JWT authentication (stateless, no UserDetailsService)
- [x] Role-based access control (ADMIN, USER, EVENT_OWNER)
- [x] Email verification with hashed tokens
- [x] Shared library via GitHub Packages (`ems-common`)
- [x] Declarative HTTP client pattern (`user-service-client`, `event-service-client`)
- [x] Docker Compose orchestration with health checks (8 services)
- [x] Multi-stage Docker builds

### Phase 2 — Event & Async Notification &check;
- [x] Event Service (CRUD, search by category / city / date range)
- [x] Category Service
- [x] Participation Service (event registration)
- [x] Apache Kafka 3.7.0 (KRaft — no ZooKeeper)
- [x] Async notification pipeline (email verification, owner welcome, registration, reminder)
- [x] Strategy Pattern handler dispatch in Notification Service
- [x] Dead Letter Queue (DLQ) with 3-retry FixedBackOff
- [x] Kafka UI monitoring dashboard
- [x] Quartz JdbcJobStore (distributed cron — horizontal scale safe)
- [x] Per-recipient partition key for message ordering
- [x] Denormalized `ownerEmail` / `participantEmail` for service autonomy
- [x] Environment-based secret management

### Phase 3 — Frontend
- [ ] Angular / React frontend application
- [ ] Full-stack integration through API Gateway
- [ ] Responsive UI with event browsing, booking, and user dashboard

### Phase 4 — AWS Cloud Deployment
- [ ] AWS ECS / EKS container orchestration
- [ ] AWS RDS for managed PostgreSQL
- [ ] AWS MSK for managed Kafka
- [ ] AWS Application Load Balancer (ALB) configuration
- [ ] AWS ECR for container registry
- [ ] CI/CD pipeline with GitHub Actions
- [ ] Infrastructure as Code (Terraform / CloudFormation)

### Phase 5 — Production Hardening
- [ ] DLQ Consumer — Slack / PagerDuty alert on failed notifications
- [ ] Idempotency — Redis `eventId` cache to prevent duplicate emails
- [ ] Circuit Breaker — Resilience4j for SMTP fast-fail
- [ ] Security vulnerability scanning (OWASP, Trivy, Snyk)
- [ ] Penetration testing — local & production
- [ ] Rate limiting and DDoS protection (AWS WAF)
- [ ] Centralized logging (ELK Stack / CloudWatch)
- [ ] Performance testing with JMeter / Gatling
- [ ] SSL/TLS termination at load balancer
- [ ] Database migration with Flyway/Liquibase
- [ ] Kafka Schema Registry (Avro — typed schema vs `Map<String,String>` payload)
- [ ] Micrometer + Prometheus metrics (consumer lag, retry count, DLQ count)

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

**Event Management System**, **Spring Boot 4** ve **Spring Cloud 2025** ile sifirdan insa edilmis, uretim ortamina hazir bir mikroservis platformudur. Sistem; servis kesfinden (Eureka), API Gateway'den, ozel JWT tabanli durumsuz (stateless) kimlik dogrulamadan, **GitHub Packages** uzerinden dagitilan ortak kutuphane modulunden, SHA-256 ile hashlenmis e-posta dogrulama tokenlarindan, rol tabanli erisim kontrolunden, **Apache Kafka ile asenkron bildirim mimarisinden**, **Quartz dagitik zamanlayicidan** ve saglik kontrolleri iceren tam Docker konteynerizasyonundan olusmaktadir.

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
                  +--------------------+--------------------+
                  |                                         |
       +----------+----------+               +-------------+----------+
       |    User Service      |               |    Event Service       |
       |    Port: 8080        |               |    Port: 8081          |
       +----------+----------+               +-------------+----------+
                  |                                         |
                  +-------------------+---------------------+
                                      |
                                      | Kafka Producer
                                      v
                            +---------------------+
                            |  Apache Kafka 3.7.0  |
                            |  (KRaft — ZK yok)    |
                            +----------+----------+
                                       |
                                       | Kafka Consumer
                                       v
                            +---------------------+
                            | Notification Service |
                            |  (Strategy Pattern)  |
                            +----------+----------+
                                       |
                                       v
                            +---------------------+
                            |   Resend SMTP        |
                            +---------------------+
```

---

# Kullanilan Teknolojiler

| Kategori | Teknoloji | Aciklama |
|:---|:---|:---|
| **Framework** | Spring Boot 4.0.5 + Spring Cloud 2025.1.1 | Temel uygulama cercevesi |
| **Dil** | Java 21 (LTS) | Programlama dili |
| **Veritabani** | PostgreSQL 17 + Spring Data JPA | Servis basina izole veri katmani |
| **Mesajlasma** | Apache Kafka 3.7.0 (KRaft) | Asenkron bildirim pipeline'i |
| **Zamanlayici** | Quartz JdbcJobStore | Dagitik cron — yatay olcekleme uyumlu |
| **Guvenlik** | Spring Security + Custom JWT (JJWT 0.13.0) | Kimlik dogrulama ve yetkilendirme |
| **Sifreleme** | BCrypt (sifre) + SHA-256 (token) | Hassas veri koruma |
| **API Gateway** | Spring Cloud Gateway WebMVC | Yonlendirme, yuk dengeleme, CORS |
| **Servis Kesfi** | Netflix Eureka | Dinamik servis kayit ve kesif |
| **Nesne Esleme** | MapStruct 1.6.3 | Derleme zamanli DTO-Entity donusumu |
| **E-posta** | Notification Service → Resend SMTP | Kafka consumer uzerinden islemsel e-posta |
| **Konteyner** | Docker + Docker Compose (8 servis) | Cok asamali build ve orkestrasyon |
| **Paket Yonetimi** | GitHub Packages | Maven artifact dagitimi |
| **Izleme** | Spring Actuator + Kafka UI | Saglik kontrolu ve mesaj izleme |

---

# Onemli Muhendislik Kararlari ve Teknik Detaylar

### 1. GitHub Packages ile Paylasimli Kutuphane (`ems-common`)
`ems-common` modulu, tum mikroservislerin ortaklasa kullandigi exception handling, error response formati, auth interceptor ve **Kafka mesaj DTO'larini** (`NotificationEvent`, `NotificationEventType`) icerir. Bu modul **GitHub Packages** uzerinde Maven artifact olarak yayinlanir.

### 2. Kafka KRaft — ZooKeeper'siz Mimari
Apache Kafka 3.7.0, **KRaft modunda** calisir. ZooKeeper tamamen stack'ten cikarilmistir. Uc listener turu farkli amaclar icin kullanilir:
- **`INTERNAL`** (29092): Docker icindeki container'lar arasi iletisim
- **`EXTERNAL`** (9092): Host makineden dis erisim
- **`CONTROLLER`** (9093): Raft lider secimi ve metadata replikasyonu

### 3. JSON Serializasyon — Type Header Devre Disi
```yaml
# Producer: type header ekleme
spring.json.add.type.headers: false

# Consumer: her mesaji bu tip olarak parse et
spring.json.value.default.type: com.example.ems_common.dto.NotificationEvent
```
Type header acik olsaydi, Consumer farkli bir package'dan gelen `NotificationEvent`'i bulamazdi — deserialization hatasi. `ems-common`'daki tek DTO + header kapali = her producer uyumlu.

### 4. Quartz JdbcJobStore — Dagitik Kilit
`@Scheduled` her node'da calisir → duplicate reminder. Quartz `isClustered: true` ile sadece bir node `QRTZ_LOCKS` tablosundan kilidi alir, job'i calistirir. `@DisallowConcurrentExecution` ayni node icerisinde ekstra guvence saglar. `PostgreSQLDelegate` zorunludur — binary blob yerine String saklama.

### 5. Strategy Pattern — Bildirim Handler'lari
`ConsumerService` if-else yerine `EnumMap<NotificationEventType, NotificationHandler>` kullanir. Yeni bildirim tipi eklemek icin yalnizca yeni bir `@Component` yazmak yeterli — mevcut koda dokunulmaz (Open/Closed).

### 6. Denormalizasyon Kararlari
- **`participantEmail`** Participation entity'sinde saklanir — her bildirimde User Service'e HTTP atmak gerekmez
- **`ownerEmail`** Event entity'sinde saklanir — etkinlik olusturulurken zaten yapilan `getUserById` cagrisi kullanilir

### 7. DLQ ile Hata Yonetimi
3 retry × 2 saniye → basarisiz mesaj `notification-events-dlq` topic'ine tasinir. **Ayri `dlqKafkaTemplate`** circular bean dependency'yi onler. DLQ mesajlari **Kafka UI** (port 8085) uzerinden izlenebilir ve replay edilebilir.

### 8. Cok Asamali Docker Build
Her Dockerfile: build asamas `maven:3.9.9-eclipse-temurin-21`, run asamasi `eclipse-temurin:21-jre`. Final image boyutu kucuk, build araclari uretim ortamina gitmez.

### 9. Docker Compose Saglik Kontrolleri
Servisler katmanlari bagimlilik sirasina gore baslar:
```
PostgreSQL (pg_isready)
  → User Service, Event Service

Eureka (actuator/health)
  → API Gateway, User Service, Event Service

Kafka (kafka-topics.sh --list)
  → Notification Service, User Service, Event Service
```

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

# 2. .env dosyasini yapilandirin

# 3. Tum servisleri baslatin (8 container)
docker compose up --build -d
```

### Servis Adresleri

| Servis | Adres |
|:---|:---|
| API Gateway | `http://localhost:8090` |
| Eureka Dashboard | `http://localhost:8761` |
| Kafka UI (DLQ izleme) | `http://localhost:8085` |
| Kafka (dis erisim) | `localhost:9092` |
| PostgreSQL - User | `localhost:5432` |
| PostgreSQL - Event | `localhost:5433` |

---

# Gelecek Vizyonu

### Faz 1 — Backend Mikroservisler &check;
- [x] Spring Boot 4 ile mikroservis mimarisi
- [x] Netflix Eureka + Spring Cloud Gateway
- [x] Ozel JWT kimlik dogrulama (stateless)
- [x] Rol tabanli erisim kontrolu
- [x] Hashlenmis tokenlarla e-posta dogrulama
- [x] GitHub Packages ile paylasimli kutuphane

### Faz 2 — Etkinlik ve Asenkron Bildirimler &check;
- [x] Event Service (CRUD, kategori, sehir, tarih aralik filtreleme)
- [x] Participation Service (etkinlige katilim)
- [x] Apache Kafka 3.7.0 (KRaft — ZooKeeper yok)
- [x] Asenkron bildirim pipeline (e-posta dogrulama, owner hosgeldin, katilim, hatirlatici)
- [x] Strategy Pattern ile handler dispatch
- [x] DLQ + 3 retry mekanizmasi
- [x] Kafka UI izleme paneli
- [x] Quartz JdbcJobStore (yatay olcekleme uyumlu cron)

### Faz 3 — Frontend
- [ ] Angular / React frontend uygulamasi
- [ ] API Gateway uzerinden full-stack entegrasyon
- [ ] Duyarli kullanici arayuzu

### Faz 4 — AWS Cloud Deployment
- [ ] AWS ECS / EKS + AWS MSK (yonetilen Kafka)
- [ ] AWS RDS yonetilen veritabani
- [ ] GitHub Actions ile CI/CD pipeline
- [ ] Terraform / CloudFormation ile IaC

### Faz 5 — Uretim Saglamiligi
- [ ] DLQ Consumer — Slack / PagerDuty alert
- [ ] Redis ile idempotency (duplicate mail engeli)
- [ ] Circuit Breaker — Resilience4j for SMTP fast-fail
- [ ] Micrometer + Prometheus metrikleri
- [ ] Guvenlik zafiyet taramasi (OWASP, Trivy)
- [ ] Performans testi (JMeter / Gatling)
- [ ] Kafka Schema Registry (Avro)

---

<div align="center">

<img src="https://capsule-render.vercel.app/api?type=waving&color=0:0d1117,50:161b22,100:1f6feb&height=120&section=footer&animation=fadeIn" width="100%"/>

**Built with determination and passion for distributed systems.**

![Profile Views](https://komarev.com/ghpvc/?username=berkayerdemsoy&color=58A6FF&style=flat-square&label=Profile+Views)

</div>

