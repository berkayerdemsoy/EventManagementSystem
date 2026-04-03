package com.example.ems_common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;

/**
 * Interceptor that propagates the JWT Authorization header from the incoming
 * HTTP request to any outgoing RestClient / RestTemplate call.
 * <p>
 * This ensures that inter-service synchronous communication carries the
 * same bearer token that the original caller provided to the gateway / upstream service.
 */
public class AuthRequestInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AuthRequestInterceptor.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    public @NonNull ClientHttpResponse intercept(@NonNull HttpRequest request,
                                                  byte @NonNull [] body,
                                                  @NonNull ClientHttpRequestExecution execution) throws IOException {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest servletRequest = attributes.getRequest();
            String authHeader = servletRequest.getHeader(AUTHORIZATION_HEADER);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                request.getHeaders().set(AUTHORIZATION_HEADER, authHeader);
                log.debug("Propagated Authorization header to outgoing request: {}",
                        request.getURI());
            }
        }
        return execution.execute(request, body);
    }
}


