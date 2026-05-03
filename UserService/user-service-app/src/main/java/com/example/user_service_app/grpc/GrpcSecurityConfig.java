package com.example.user_service_app.grpc;

import com.example.ems_common.interceptor.GrpcJwtServerInterceptor;
import com.example.ems_common.security.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * gRPC Güvenlik Konfigürasyonu — user-service-app
 *
 * <p>Spring gRPC 1.0.3, uygulama context'indeki tüm {@link io.grpc.ServerInterceptor}
 * bean'lerini otomatik olarak global interceptor olarak kaydeder.
 * {@link GrpcJwtServerInterceptor}'ı burada bean olarak tanımlamak yeterlidir.
 */
@Configuration
public class GrpcSecurityConfig {

    /**
     * gRPC JWT doğrulama interceptor'ı.
     *
     * <p>Bu bean, tüm gRPC servis metotlarından önce çalışır:
     * <ol>
     *   <li>Metadata'dan "authorization" header'ını okur</li>
     *   <li>JWT'yi doğrular</li>
     *   <li>SecurityContextHolder'ı doldurur</li>
     *   <li>İşlem sonrası try-finally ile clearContext() yapar</li>
     * </ol>
     */
    @Bean
    public GrpcJwtServerInterceptor grpcJwtServerInterceptor(JwtUtil jwtUtil) {
        return new GrpcJwtServerInterceptor(jwtUtil);
    }
}

