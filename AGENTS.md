# AGENTS.md — Event Management System

## Tech Stack
- **Spring Boot 4.0.5** / Spring Cloud 2025.1.1 / Java 21 / Maven 3.9+
- **PostgreSQL 17** (per-service isolation, port 5432/5433)
- **Apache Kafka 3.7.0** (KRaft, no ZooKeeper)
- **Quartz** (JdbcJobStore, clustered)
- Custom JWT (JJWT 0.13.0) — stateless, no UserDetailsService

## Monorepo Structure
```
ems-common/                    # GitHub Packages — shared lib (exceptions, interceptors, NotificationEvent DTO)
UserService/
  user-service-client/        # Declarative HTTP client + DTOs (mvn install after ems-common)
  user-service-app/           # App + gRPC server (port 9090)
api-gateway/                   # Spring Cloud Gateway WebMVC (port 8090)
EurekaServer/                  # Service discovery (port 8761)
event-service/
  event-service-client/       # Declarative HTTP client + DTOs
  event-service-app/         # App + gRPC client → user-service
notification-service/          # Kafka consumer (no DB)
```

## Build & Run

### Docker (full stack)
```bash
docker compose up --build -d
```

### Local development build order
```bash
# 1) ems-common first (shared lib)
cd ems-common && mvn clean install -DskipTests

# 2) user-service-client
cd ../UserService/user-service-client && mvn clean install -DskipTests

# 3) event-service-client (depends on user-service-client)
cd ../../event-service/event-service-client && mvn clean install -DskipTests

# 4) Run services (Eureka → gateway → user-service → event-service → notification-service)
```

### UserService Dockerfile builds both modules
```bash
docker build ./UserService --build-arg GITHUB_ACTOR=... --build-arg GITHUB_TOKEN=...
```

### EventService Dockerfile builds client + app (context: project root)
```bash
docker build -f event-service/Dockerfile . --build-arg GITHUB_ACTOR=... --build-arg GITHUB_TOKEN=...
```

## Ports
| Service | Port |
|:---|---:|
| API Gateway | 8090 |
| Eureka | 8761 |
| User Service (HTTP) | 8080 |
| User Service (gRPC) | 9090 |
| Event Service | 8081 |
| Notification Service | 8083 |
| Kafka (external) | 9092 |
| Kafka UI | 8085 |
| PostgreSQL (user) | 5432 |
| PostgreSQL (event) | 5433 |

## Critical Architecture Notes

### CORS — Gateway only, YAML nesting matters
```yaml
# Correct (WebMVC variant):
spring.cloud.gateway.server.webmvc.globalcors.cors-configurations[/**]...

# Wrong (silently ignored, no preflight headers):
spring.cloud.gateway.globalcors.cors-configurations[/**]...
```

### Kafka JSON serialization — type header disabled
- Producers: `spring.json.add.type.headers: false`
- Consumer: `spring.json.value.default.type: com.example.ems_common.dto.NotificationEvent`
- If type headers are enabled, consumer deserializes to wrong class and fails.

### Kafka partition key
```java
kafkaTemplate.send(topic, event.getRecipientEmail(), event);  // recipientEmail = partition key
```
Same recipient always lands on same partition → delivery order guaranteed.

### gRPC inter-service communication
- UserService gRPC server: port `9090` (env `GRPC_PORT`), registered via `@GrpcService`
- EventService uses `GrpcChannelFactory` with `discovery:///user-service-app`
- JWT propagated via `GrpcJwtClientInterceptor` (reads from `RequestContextHolder`)
- **gRPC server does NOT validate incoming JWT** — this is a known gap. Add interceptor if needed.

### Quartz distributed scheduler
- `@DisallowConcurrentExecution` + JdbcJobStore + `isClustered: true`
- Uses `PostgreSQLDelegate` (required, not standard delegate)
- Only one node acquires lock and runs job → no duplicate reminders.

### email verification → token renewal
`GET /users/confirm-email?token=...` returns `AuthResponseDto` with a new JWT (the user's existing token is NOT invalidated — stateless JWT). Update `localStorage` on frontend to use the new token.

### Database migrations (Liquibase)
- `user-service-app`: `db/changelog/db.changelog-master.yaml`
- `event-service-app`: `db/changelog/db.changelog-master.yaml`
- Migrations run automatically on startup.

## Maven Annotation Processor Order (Lombok + MapStruct)
Must be in this order in `maven-compiler-plugin`:
1. `lombok`
2. `lombok-mapstruct-binding`
3. `mapstruct-processor`

## Secrets & Environment
All secrets via `.env` (git-ignored). See `.env.example` for template. Key vars: `JWT_SECRET`, `RESEND_API_KEY`, `ADMIN_USERNAMES`, `ADMIN_PASSWORDS`, `GITHUB_TOKEN` (for GitHub Packages Maven auth).

## Notification Service — adding new event types
1. Add value to `NotificationEventType` enum in `ems-common`
2. Create new `NotificationHandler` implementing `NotificationHandler`
3. Annotate with `@Component` — `ConsumerService` auto-discovers via `@Autowired List<NotificationHandler>`
4. No changes to `KafkaConfig`, `ConsumerService`, or producers.