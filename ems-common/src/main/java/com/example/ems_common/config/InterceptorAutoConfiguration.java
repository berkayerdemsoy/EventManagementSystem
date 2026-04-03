package com.example.ems_common.config;

import com.example.ems_common.interceptor.AuthRequestInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration that registers the {@link AuthRequestInterceptor} bean.
 * It is only activated when the servlet request classes are on the classpath
 * (i.e. in a web-mvc service, not in the reactive gateway).
 */
@AutoConfiguration
@ConditionalOnClass(name = "jakarta.servlet.http.HttpServletRequest")
public class InterceptorAutoConfiguration {

    @Bean
    public AuthRequestInterceptor authRequestInterceptor() {
        return new AuthRequestInterceptor();
    }
}

