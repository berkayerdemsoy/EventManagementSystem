package com.example.event_service_app.configs;

import com.example.ems_common.interceptor.GrpcJwtClientInterceptor;
import com.example.user_service_client.grpc.UserGrpcServiceGrpc;
import io.grpc.Channel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

/**
 * gRPC Client Konfigürasyonu — event-service-app
 *
 * <p>Kanal, Spring gRPC'nin {@link GrpcChannelFactory} altyapısı üzerinden kurulur.
 * Channel özelliklerini (adres, load-balancing politikası, TLS) doğrudan Java kodunda
 * tanımlamak yerine {@code spring.grpc.client.channels.user-service} bloğu
 * (application.yaml) üzerinden yönetiyoruz.
 *
 * <p>Adres {@code discovery:///user-service-app} olarak yapılandırıldığında
 * Spring Cloud Discovery (Eureka) NameResolver devreye girer:
 * Eureka'ya kayıtlı tüm {@code user-service-app} instance'larını bulur ve
 * Eureka metadata'sındaki {@code grpc.port} değerini okuyarak doğru porta bağlanır.
 * {@code round_robin} politikası ile yatay ölçekleme (multiple replicas) desteklenir.
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
     * <p>Kanal adı {@code "user-service"}, {@code spring.grpc.client.channels.user-service}
     * bloğuyla eşleşir. GrpcChannelFactory bu bloğu okuyarak discovery + round_robin
     * load-balanced bir kanal döner.
     */
    @Bean
    public UserGrpcServiceGrpc.UserGrpcServiceBlockingStub userGrpcServiceBlockingStub(
            GrpcChannelFactory channelFactory,
            GrpcJwtClientInterceptor grpcJwtClientInterceptor) {

        Channel channel = channelFactory.createChannel("user-service");

        return UserGrpcServiceGrpc
                .newBlockingStub(channel)
                .withInterceptors(grpcJwtClientInterceptor);
    }
}
