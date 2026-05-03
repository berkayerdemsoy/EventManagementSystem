package com.example.ems_common.interceptor;

import io.grpc.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * gRPC Client Interceptor — JWT Propagation (HTTP → gRPC)
 *
 * <p>Mevcut Tomcat thread'indeki HTTP isteğinin "Authorization: Bearer …" başlığını okuyarak
 * giden gRPC çağrısının Metadata'sına enjekte eder. Bu, {@link AuthRequestInterceptor}'un
 * gRPC karşılığıdır.
 *
 * <p><b>Thread Güvenliği Notu</b>: Bu interceptor, {@code BlockingStub} kullandığı varsayımıyla
 * tasarlanmıştır. BlockingStub, gRPC çağrısını <em>aynı</em> Tomcat iş parçacığı üzerinde
 * bloklanarak başlatır; dolayısıyla {@link RequestContextHolder} (ThreadLocal tabanlı) mevcut
 * HTTP isteğini doğru şekilde görür. Eğer ileride async stub ({@code FutureStub}) yada
 * {@code @Async}/{@code WebFlux} ile kullanılacaksa, {@code DelegatingSecurityContextExecutor}
 * veya manuel token aktarımı gerekir.
 */
public class GrpcJwtClientInterceptor implements ClientInterceptor {

    private static final Logger log = LoggerFactory.getLogger(GrpcJwtClientInterceptor.class);

    /**
     * gRPC metadata anahtarı — HTTP/2 convention'ı gereği lowercase.
     * Server tarafındaki {@link GrpcJwtServerInterceptor#AUTHORIZATION_KEY} ile aynı.
     */
    static final Metadata.Key<String> AUTHORIZATION_KEY =
            Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER);

    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions,
            Channel next) {

        return new ForwardingClientCall.SimpleForwardingClientCall<>(next.newCall(method, callOptions)) {

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                attachAuthHeader(headers);
                super.start(responseListener, headers);
            }
        };
    }

    /**
     * Mevcut Tomcat thread'inin HTTP isteğinden token'ı okuyup gRPC headers'a ekler.
     *
     * <p>RequestContextHolder null dönerse (background thread, scheduled job vb.) sessizce
     * devam eder — token eklenmez ama exception fırlatılmaz.
     */
    private void attachAuthHeader(Metadata headers) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            log.debug("gRPC client interceptor: HTTP request context bulunamadı " +
                      "(async/background thread); Authorization header eklenmeyecek");
            return;
        }

        HttpServletRequest request = attributes.getRequest();
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            headers.put(AUTHORIZATION_KEY, authHeader);
            log.debug("gRPC outbound metadata'ya Authorization header eklendi");
        } else {
            log.debug("gRPC client interceptor: gelen HTTP isteğinde Bearer token yok");
        }
    }
}

