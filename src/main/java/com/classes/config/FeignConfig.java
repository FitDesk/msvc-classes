package com.classes.config;

import feign.Logger;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración para OpenFeign
 * Incluye logging y propagación de tokens JWT
 */
@Configuration
@RequiredArgsConstructor
public class FeignConfig {

    private final FeignClientInterceptor feignClientInterceptor;

    /**
     * Configura el nivel de logging de Feign
     * BASIC: Log solo método, URL, código de respuesta y tiempo de ejecución
     * FULL: Log completo de requests y responses
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    /**
     * Registra el interceptor para propagar tokens JWT
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return feignClientInterceptor;
    }
}
