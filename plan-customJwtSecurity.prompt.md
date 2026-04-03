# Plan: UserService İçine Custom JWT Security

UserService'e `spring-boot-starter-security` + `jjwt` eklenerek login'den JWT token dönülecek, endpoint'ler role-based korunacak, şifreler BCrypt'e geçirilecek.

## Steps

1. **Bağımlılıkları ekle** — [pom.xml](./UserService/user-service-app/pom.xml)'e `spring-boot-starter-security`, `jjwt-api`, `jjwt-impl` (runtime), `jjwt-jackson` (runtime) bağımlılıkları ekle (jjwt version `0.12.6`). [application.yaml](./UserService/user-service-app/src/main/resources/application.yaml)'e `jwt.secret` (env: `JWT_SECRET`, min 256-bit) ve `jwt.expiration` (env: `JWT_EXPIRATION`, default `3600000` ms = 1 saat) property'leri ekle.

2. **`JwtUtil` sınıfı oluştur** — `configs/security/JwtUtil.java` yeni dosya. `@Component`, constructor'da `@Value("${jwt.secret}")` ve `@Value("${jwt.expiration}")`. 4 metod: `generateToken(Long userId, String username, String role)` → `Jwts.builder()` ile subject=userId, claims=username+role, expiration set edip compact; `extractClaims(String token)` → `Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload()`; `isTokenValid(String token)` → try-catch JwtException; `extractUserId`, `extractUsername`, `extractRole` helper metodları.

3. **`JwtAuthFilter` oluştur** — `configs/security/JwtAuthFilter.java` yeni dosya. `OncePerRequestFilter` extend eder. `doFilterInternal` içinde: `Authorization` header'ı al, `Bearer ` prefix kontrolü, `jwtUtil.isTokenValid()`, geçerliyse userId/username/role extract et, `UsernamePasswordAuthenticationToken` oluştur (`principal`=userId, `authorities`=`ROLE_` + role), `SecurityContextHolder.getContext().setAuthentication()` set et, `filterChain.doFilter()`.

4. **`SecurityConfig` oluştur** — `configs/security/SecurityConfig.java` yeni dosya. `@Configuration @EnableWebSecurity`. `SecurityFilterChain` bean: `csrf.disable()`, `sessionManagement=STATELESS`, `authorizeHttpRequests` ile: `permitAll` → `/users/login`, `/users/create`, `/users/confirm-email`, `/actuator/**`; `hasRole("ADMIN")` → `/users/all`, `DELETE /users/**`; `authenticated()` → `anyRequest`. `addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)`. Ayrı bean: `PasswordEncoder` → `new BCryptPasswordEncoder()`.

5. **`AuthResponseDto` oluştur** — [user-service-client dto](./UserService/user-service-client/src/main/java/com/example/user_service_client/dto) paketine yeni `AuthResponseDto.java`: iki alan — `String token` ve `UserResponseDto user`. Lombok `@Data @AllArgsConstructor @NoArgsConstructor`.

6. **`UserService` interface güncelle** — [UserService.java](./UserService/user-service-app/src/main/java/com/example/user_service_app/service/UserService.java) satır 18: `login` dönüş tipini `UserResponseDto` → `AuthResponseDto` olarak değiştir.

7. **`UserServiceImpl` güncelle** — [UserServiceImpl.java](./UserService/user-service-app/src/main/java/com/example/user_service_app/serviceImpl/UserServiceImpl.java) üzerinde 3 değişiklik:
   - Constructor'a `PasswordEncoder` ve `JwtUtil` inject et.
   - **`createUser`**: `userRepository.save()` öncesinde `user.setPassword(passwordEncoder.encode(dto.getPassword()))` ile şifreyi hashle. Admin kontrolü encode'dan **önce** plain-text `dto.getPassword()` ile yapılmaya devam etsin — çünkü env'deki admin şifreleri plain-text.
   - **`login`**: `user.getPassword().equals(dto.getPassword())` → `passwordEncoder.matches(dto.getPassword(), user.getPassword())` olarak değiştir. **Lazy migration** ekle: eğer BCrypt matches false ise VE plain-text equals true ise → şifreyi BCrypt ile hashle, kaydet, devam et. Dönüş tipini `AuthResponseDto` yap: `jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole().name())` ile token üret, `userMapper.toResponseDto(user)` ile user bilgisi, ikisini `AuthResponseDto`'ya koy ve dön.

8. **`UserController` güncelle** — [UserController.java](./UserService/user-service-app/src/main/java/com/example/user_service_app/controller/UserController.java) satır 66-69: `login` metodunun dönüş tipini `ResponseEntity<AuthResponseDto>` olarak değiştir.

9. **`UserServiceClient` güncelle** — [UserServiceClient.java](./UserService/user-service-client/src/main/java/com/example/user_service_client/client/UserServiceClient.java) satır 39: `void login(...)` → `AuthResponseDto login(...)` olarak değiştir.

10. **`docker-compose.yml` güncelle** — [docker-compose.yml](./docker-compose.yml) user-service environment bölümüne `JWT_SECRET: ${JWT_SECRET}` ekle. `.env` dosyasına da `JWT_SECRET=<min-32-karakter-random-string>` ekle.

## Further Considerations

1. **Admin şifreleri**: Şu an `ADMIN_PASSWORDS` env'de plain-text. Bunları da BCrypt hash olarak saklamak isterseniz, `createUser`'daki admin kontrolünü `passwordEncoder.matches(dto.getPassword(), adminProperties.getPasswords().get(adminIndex))` olarak değiştirmek yeterli — ama o zaman env'e BCrypt hash koymanız lazım.
2. **Mapper password mapping**: `UserMapper.toEntity()` password'ü de map'liyor. `createUser`'da encode'dan sonra `user.setPassword(...)` ile override ediyoruz — çalışır ama isterseniz mapper'a `@Mapping(target = "password", ignore = true)` eklenebilir, daha temiz olur.
3. **Mevcut DB'deki plain-text şifreler**: Step 7'deki lazy migration stratejisi mevcut kullanıcıların ilk login'lerinde şifrelerini otomatik BCrypt'e çevirecek — ekstra migration script'e gerek yok.

