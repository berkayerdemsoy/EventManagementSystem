package com.example.ems_common.interceptor;

import com.example.ems_common.security.JwtUtil;
import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

/**
 * gRPC Server Interceptor — JWT Doğrulama & SecurityContext Yönetimi
 *
 * <p>Üç kritik güvenlik kuralını uygular:
 *
 * <ol>
 *   <li><b>SecurityContext doldurma</b>: Gelen gRPC Metadata'sındaki "authorization" key'inden
 *       Bearer token'ı okur, JwtUtil ile doğrular ve SecurityContextHolder'ı doldurur.
 *       Bu olmazsa downstream @PreAuthorize anotasyonları ve SecurityUtils.getCurrentUserId()
 *       çalışmaz.
 *
 *   <li><b>Thread Pool Güvenlik Açığı Önlemi</b>: gRPC, Netty iş parçacıklarını yeniden
 *       kullanır. Her Listener callback (onMessage, onHalfClose) etrafında mutlaka
 *       try-finally bloğu içinde SecurityContextHolder.clearContext() çağrılır; aksi
 *       takdirde bir sonraki istekte eski kullanıcının yetkileri kalır — kritik güvenlik açığı!
 *
 *   <li><b>interceptCall vs Listener sorunu</b>: interceptCall'da SecurityContext set etmek
 *       yetmez çünkü asıl iş Listener callback'lerinde (onHalfClose) gerçekleşir.
 *       Bu yüzden her callback ayrı ayrı sarılır.
 * </ol>
 */
public class GrpcJwtServerInterceptor implements ServerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(GrpcJwtServerInterceptor.class);

    /**
     * HTTP/2 ve gRPC metadata anahtarları lowercase kullanır.
     * HTTP header'daki "Authorization: Bearer …" karşılığı gRPC'de "authorization" key'idir.
     */
    static final Metadata.Key<String> AUTHORIZATION_KEY =
            Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER);

    private final JwtUtil jwtUtil;

    public GrpcJwtServerInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        // SecurityContext'i metadata'dan oluştur (token yoksa / geçersizse empty context)
        SecurityContext securityContext = buildSecurityContext(headers);

        /*
         * next.startCall() çağrısı Listener'ı başlatır ama asıl işlem
         * onHalfClose() içinde gerçekleşir. Bu yüzden interceptCall seviyesinde
         * SecurityContext set etmek yetmez — her Listener callback'ini sarmamız gerekir.
         */
        ServerCall.Listener<ReqT> delegate = next.startCall(call, headers);

        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(delegate) {

            /**
             * Unary ve client-streaming RPC'lerde request mesajı burada gelir.
             * SecurityContext bu thread için set edilip işlem sonrası temizlenir.
             */
            @Override
            public void onMessage(ReqT message) {
                SecurityContextHolder.setContext(securityContext);
                try {
                    super.onMessage(message);
                } finally {
                    SecurityContextHolder.clearContext(); // ZORUNLU — thread pool leak önlemi
                }
            }

            /**
             * Unary RPC için asıl iş burada yapılır.
             * @PreAuthorize ve SecurityUtils.getCurrentUserId() bu callback sırasında çalışır.
             */
            @Override
            public void onHalfClose() {
                SecurityContextHolder.setContext(securityContext);
                try {
                    super.onHalfClose();
                } finally {
                    SecurityContextHolder.clearContext(); // ZORUNLU — thread pool leak önlemi
                }
            }

            /**
             * İptal durumunda context temizle.
             */
            @Override
            public void onCancel() {
                try {
                    super.onCancel();
                } finally {
                    SecurityContextHolder.clearContext();
                }
            }

            /**
             * Başarılı tamamlanmada da temizle (savunmacı programlama).
             */
            @Override
            public void onComplete() {
                try {
                    super.onComplete();
                } finally {
                    SecurityContextHolder.clearContext();
                }
            }
        };
    }

    /**
     * gRPC Metadata'sından token'ı çıkarır, doğrular ve SecurityContext oluşturur.
     * Token yoksa veya geçersizse boş (anonim) bir SecurityContext döner —
     * böylece interceptor downstream hataya yol açmaz; yetki kontrolü @PreAuthorize'a bırakılır.
     */
    private SecurityContext buildSecurityContext(Metadata headers) {
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();

        String authHeader = headers.get(AUTHORIZATION_KEY);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("gRPC isteğinde Authorization header bulunamadı");
            return ctx;
        }

        String token = authHeader.substring(7);

        try {
            if (!jwtUtil.isTokenValid(token)) {
                log.warn("gRPC isteğinde geçersiz JWT token");
                return ctx;
            }

            Long userId = jwtUtil.extractUserId(token);
            String role  = jwtUtil.extractRole(token);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                    );

            ctx.setAuthentication(authentication);
            log.debug("gRPC SecurityContext dolduruldu — userId={}, role={}", userId, role);

        } catch (Exception e) {
            log.warn("gRPC JWT ayrıştırma hatası: {}", e.getMessage());
        }

        return ctx;
    }
}

