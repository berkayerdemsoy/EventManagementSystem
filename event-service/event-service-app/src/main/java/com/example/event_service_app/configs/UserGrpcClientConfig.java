package com.example.event_service_app.configs;

import com.example.ems_common.interceptor.GrpcJwtClientInterceptor;
import com.example.user_service_client.grpc.UserGrpcServiceGrpc;
import io.grpc.Channel;
import io.grpc.NameResolverProvider;
import io.grpc.NameResolverRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.grpc.client.GrpcChannelFactory;

/**
 * gRPC Client Konfigürasyonu — event-service-app
 *
 * <p>Kanal, Spring gRPC 1.0.x'in belgelenmiş API'si olan {@link GrpcChannelFactory}
 * üzerinden kurulur. {@code spring.grpc.client.channels.user-service} bloğundaki
 * adres ({@code discovery:///user-service-app}) custom NameResolverProvider ile
 * Spring Cloud DiscoveryClient üzerinden çözülür.
 *
 * <p>Stub standart bir Spring Bean olarak expose edilir;
 * {@code @RequiredArgsConstructor} sorunsuz inject eder.
 */
@Configuration
public class UserGrpcClientConfig {

    @Bean
    public NameResolverProvider discoveryNameResolverProvider(DiscoveryClient discoveryClient) {
        NameResolverProvider provider = new DiscoveryClientNameResolverProvider(discoveryClient);
        NameResolverRegistry.getDefaultRegistry().register(provider);
        return provider;
    }

    /**
     * gRPC JWT client interceptor — Authorization header'ını gRPC metadata'sına kopyalar.
     */
    @Bean
    public GrpcJwtClientInterceptor grpcJwtClientInterceptor() {
        return new GrpcJwtClientInterceptor();
    }

    /**
     * user-service-app için gRPC blocking stub.
     *
     * <p>Kanal adı {@code "user-service"}, {@code spring.grpc.client.channels.user-service}
     * bloğuyla eşleşir. {@link GrpcChannelFactory} bu bloğu okuyarak discovery + round_robin
     * load-balanced bir kanal döner; {@link GrpcJwtClientInterceptor} stub'a eklenir.
     */
    @Bean
    @DependsOn("discoveryNameResolverProvider")
    public UserGrpcServiceGrpc.UserGrpcServiceBlockingStub userGrpcServiceBlockingStub(
            GrpcChannelFactory channelFactory,
            GrpcJwtClientInterceptor grpcJwtClientInterceptor) {

        Channel channel = channelFactory.createChannel("user-service");

        return UserGrpcServiceGrpc
                .newBlockingStub(channel)
                .withInterceptors(grpcJwtClientInterceptor);
    }
}
