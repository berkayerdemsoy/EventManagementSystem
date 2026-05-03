package com.example.event_service_app.configs;

import com.example.ems_common.interceptor.GrpcJwtClientInterceptor;
import com.example.user_service_client.grpc.UserGrpcServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * gRPC Client Konfigürasyonu — event-service-app
 *
 * <p>Spring gRPC 1.0.3, {@code lb://} şeması için NameResolverProvider içermediğinden
 * kanal doğrudan {@link ManagedChannelBuilder#forAddress(String, int)} ile kurulur.
 * Host ve port, ortam değişkenleri veya application.yaml üzerinden yapılandırılır.
 *
 * <p>Docker Compose'da {@code USER_SERVICE_GRPC_HOST=user-service} (Docker DNS)
 * ve {@code USER_SERVICE_GRPC_PORT=9090} olarak set edilmesi yeterlidir.
 *
 * <p><b>JWT Propagation</b>: {@link GrpcJwtClientInterceptor}, mevcut HTTP isteğinin
 * Authorization header'ını gRPC metadata'sına kopyalar. BlockingStub kullandığımız için
 * aynı Tomcat thread'inde çalışır ve ThreadLocal sorun yaratmaz.
 */
@Configuration
public class UserGrpcClientConfig {

    /**
     * gRPC JWT client interceptor bean'i.
     * Tüm giden gRPC çağrılarına Authorization metadata header'ı ekler.
     */
    @Bean
    public GrpcJwtClientInterceptor grpcJwtClientInterceptor() {
        return new GrpcJwtClientInterceptor();
    }

    /**
     * user-service-app için gRPC blocking stub.
     *
     * <p>Kanal, {@code grpc.client.user-service.host} ve
     * {@code grpc.client.user-service.port} property'lerinden kurulur.
     * Varsayılanlar: host=localhost, port=9090 (local development).
     * Docker Compose'da host=user-service olarak override edilir.
     */
    @Bean
    public UserGrpcServiceGrpc.UserGrpcServiceBlockingStub userGrpcServiceBlockingStub(
            @Value("${grpc.client.user-service.host:localhost}") String host,
            @Value("${grpc.client.user-service.port:9090}") int port,
            GrpcJwtClientInterceptor grpcJwtClientInterceptor) {

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();

        return UserGrpcServiceGrpc
                .newBlockingStub(channel)
                .withInterceptors(grpcJwtClientInterceptor);
    }
}
