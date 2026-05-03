package com.example.user_service_app.grpc;

import com.example.ems_common.exceptions.ForbiddenException;
import com.example.ems_common.exceptions.NotFoundException;
import com.example.user_service_app.service.UserService;
import com.example.user_service_client.dto.UserResponseDto;
import com.example.user_service_client.grpc.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

/**
 * gRPC Server — UserGrpcService implementasyonu.
 *
 * <p>Bu sınıf, event-service'in internal çağrıları için uç noktaları sağlar.
 * REST Controller'ın gRPC karşılığıdır ve mevcut {@link UserService} business logic'ini
 * yeniden kullanır — kod tekrarı olmadan.
 *
 * <p><b>Güvenlik</b>: {@link com.example.ems_common.interceptor.GrpcJwtServerInterceptor}
 * her çağrıdan önce SecurityContext'i doldurur. Bu sınıftaki metotlar doğrudan
 * {@code SecurityUtils.getCurrentUserId()} veya {@code @PreAuthorize} kullanabilir.
 *
 * <p><b>Thread Güvenliği</b>: Interceptor, her Listener callback'i etrafında
 * try-finally ile SecurityContextHolder.clearContext() çağırır. Bu sınıf, security
 * temizliğini interceptor'a devreder.
 *
 * <p><b>Hata Yönetimi</b>: Uygulama exception'ları gRPC Status kodlarına dönüştürülür.
 * Bu dönüşüm yapılmazsa gRPC istemcisi anlamsız UNKNOWN hatası alır.
 */
@Slf4j
@GrpcService
@RequiredArgsConstructor
public class UserGrpcServiceImpl extends UserGrpcServiceGrpc.UserGrpcServiceImplBase {

    private final UserService userService;

    /**
     * Kullanıcıyı ID'ye göre getirir.
     * event-service'in EventServiceImpl.createEvent() içinde çağırır.
     */
    @Override
    public void getUserById(GetUserByIdRequest request,
                            StreamObserver<UserGrpcResponse> responseObserver) {
        log.debug("gRPC GetUserById — userId={}", request.getId());
        try {
            UserResponseDto user = userService.getUserById(request.getId());
            responseObserver.onNext(toGrpcResponse(user));
            responseObserver.onCompleted();
        } catch (NotFoundException e) {
            log.warn("gRPC GetUserById — kullanıcı bulunamadı: {}", e.getMessage());
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        } catch (Exception e) {
            log.error("gRPC GetUserById — beklenmeyen hata", e);
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Internal server error")
                            .asRuntimeException()
            );
        }
    }

    /**
     * Kullanıcıyı EVENT_OWNER rolüne yükseltir ve güncel kullanıcı bilgisini döner.
     *
     * <p>Tek gRPC round-trip ile hem "beOwner" hem "getUserById" işlemi yapılır;
     * event-service ayrıca getUserById çağırmak zorunda kalmaz.
     *
     * <p>Güvenlik: {@link UserService#beOwner(Long)} @PreAuthorize anotasyonuna sahiptir.
     * Interceptor SecurityContext'i doldurduğu için bu kontrol doğru çalışır.
     */
    @Override
    public void beOwner(BeOwnerRequest request,
                        StreamObserver<UserGrpcResponse> responseObserver) {
        log.debug("gRPC BeOwner — userId={}", request.getId());
        try {
            // beOwner role'ü günceller (void) → sonrasında güncel kullanıcıyı çek
            userService.beOwner(request.getId());
            UserResponseDto updatedUser = userService.getUserById(request.getId());
            responseObserver.onNext(toGrpcResponse(updatedUser));
            responseObserver.onCompleted();
        } catch (NotFoundException e) {
            log.warn("gRPC BeOwner — kullanıcı bulunamadı: {}", e.getMessage());
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        } catch (ForbiddenException e) {
            log.warn("gRPC BeOwner — yetki hatası: {}", e.getMessage());
            responseObserver.onError(
                    Status.PERMISSION_DENIED
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        } catch (Exception e) {
            log.error("gRPC BeOwner — beklenmeyen hata", e);
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Internal server error")
                            .asRuntimeException()
            );
        }
    }

    // ─── Mapper ──────────────────────────────────────────────────────────────

    /**
     * {@link UserResponseDto} → {@link UserGrpcResponse} dönüşümü.
     * Protobuf null değer kabul etmez; null alanlar boş string'e dönüştürülür.
     */
    private UserGrpcResponse toGrpcResponse(UserResponseDto dto) {
        return UserGrpcResponse.newBuilder()
                .setId(dto.getId())
                .setUsername(nullSafe(dto.getUsername()))
                .setEmail(nullSafe(dto.getEmail()))
                .setRole(dto.getRole() != null ? dto.getRole().name() : "")
                .setVerified(dto.isVerified())
                .setFirstName(nullSafe(dto.getFirstName()))
                .setLastName(nullSafe(dto.getLastName()))
                .setPhoneNumber(nullSafe(dto.getPhoneNumber()))
                .build();
    }

    private String nullSafe(String value) {
        return value != null ? value : "";
    }
}

