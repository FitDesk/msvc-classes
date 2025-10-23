package com.classes.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * Interceptor para agregar el token JWT a las peticiones de Feign
 * Propaga el token de autenticación del usuario actual a los microservicios
 */
@Component
@Slf4j
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            String token = jwt.getTokenValue();
            
            log.debug("🔐 Agregando token JWT a la petición Feign: {}", 
                    template.method() + " " + template.url());
            
            template.header("Authorization", "Bearer " + token);
        } else {
            log.warn("⚠️ No hay token JWT disponible para la petición Feign: {}", 
                    template.method() + " " + template.url());
        }
    }
}
