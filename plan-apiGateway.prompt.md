## Plan: API Gateway + Eureka Entegrasyonu

JWT security hazır olan UserService'in önüne Spring Cloud Gateway ekleyerek tüm istekleri tek noktadan yönetmek, Eureka üzerinden servis keşfi yapmak ve Angular frontend için CORS desteği sağlamak.

---

### Steps

#### ✅ 1. `ApiGateway` Maven projesi oluştur
- Root'ta `api-gateway/` klasörü oluşturuldu.
- `pom.xml`: `spring-cloud-starter-gateway-server-webmvc`, `spring-cloud-starter-netflix-eureka-client`, `spring-boot-starter-actuator`.
- Spring Boot `4.0.5`, Spring Cloud `2025.1.1`, Java 21 — mevcut servislerle uyumlu.

#### ✅ 2. Gateway `application.yml` yapılandır
- Port: `${SERVER_PORT:8090}`
- Eureka client: `defaultZone: ${EUREKA_URL:http://localhost:8761/eureka}`
- Route: `user-service-app` → `lb://user-service-app`, predicate `Path=/users/**`
- CORS: `allowedOrigins: ${CORS_ALLOWED_ORIGINS:http://localhost:4200}`, `allowedMethods: *`, `allowedHeaders: *`, `allowCredentials: true`, `exposedHeaders: Authorization`

#### ✅ 3. Gateway main class oluştur
- `com.example.api_gateway.ApiGatewayApplication`: `@SpringBootApplication` + `@EnableDiscoveryClient`

#### ✅ 4. Gateway Dockerfile oluştur
- Multi-stage build: `maven:3.9.9-eclipse-temurin-21` → `eclipse-temurin:21-jre`, port `8090` expose.

#### ✅ 5. `docker-compose.yml`'e gateway servisini ekle
- `api-gateway`: port `8090:8090`, `EUREKA_URL`, `CORS_ALLOWED_ORIGINS` env.
- `depends_on: eureka-server (service_healthy)`, aynı `microservices-network`.
- UserService port `ports: 8080:8080` → `expose: 8080` (sadece internal network, dışarıya gateway üzerinden).

#### ✅ 6. `ems-common` modülüne `AuthRequestInterceptor` ekle
- `com.example.ems_common.interceptor.AuthRequestInterceptor`: `ClientHttpRequestInterceptor` implementasyonu.
- Gelen HTTP isteğinden `Authorization: Bearer ...` header'ını alıp giden `RestClient` isteğine otomatik ekler.
- `InterceptorAutoConfiguration` ile auto-configuration olarak register edildi.
- Sadece servlet ortamında aktif (`@ConditionalOnClass(HttpServletRequest.class)`).
- `ems-common` versiyonu `1.0.5` → `1.0.6` olarak güncellendi.

#### ✅ 7. `UserServiceClientConfig`'e interceptor entegrasyonu
- `RestClient.builder().requestInterceptors(...)` ile `AuthRequestInterceptor` bean'i otomatik enjekte edildi.
- Servisler arası senkron çağrılarda JWT token propagation sağlandı.

---

### Mimari Akış

```
Angular (4200) → API Gateway (8090) → [Eureka Discovery] → UserService (8080)
                       ↓ CORS                                    ↓ JWT Auth
                  allowedOrigins                           SecurityFilterChain
```

**Servisler Arası İletişim:**
```
ServiceA → RestClient + AuthRequestInterceptor → ServiceB
              (JWT token otomatik propagation)
```

---

### Further Considerations

1. **Gateway JWT Filter** — Şu an gateway sadece proxy. İleride gateway seviyesinde de token validation eklenebilir (public route bypass ile).
2. **Angular Frontend** — Root'ta `frontend/` klasöründe Angular projesi oluşturulacak. `docker-compose`'a nginx container olarak eklenecek.
3. **Rate Limiting / Circuit Breaker** — `spring-cloud-starter-circuitbreaker-reactor-resilience4j` ileride eklenebilir.
4. **ems-common publish** — `1.0.6` versiyonunu GitHub Packages'a deploy etmek gerekecek: `cd ems-common && mvn deploy`
