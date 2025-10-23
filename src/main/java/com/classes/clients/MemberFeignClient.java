package com.classes.clients;

import com.classes.config.FeignConfig;
import com.classes.dtos.external.MemberInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

/**
 * Cliente Feign para comunicarse con msvc-members
 * 
 * NOTA: Si msvc-members no está corriendo, este cliente fallará con "Connection refused"
 * Asegúrate de que msvc-members esté registrado en Eureka o corriendo en el puerto especificado
 */
@FeignClient(
    name = "msvc-members",  // Usa Eureka para descubrir el servicio
    // url = "http://localhost:9098",  // Comentado: usa Eureka en lugar de URL fija
    configuration = FeignConfig.class
)
public interface MemberFeignClient {

    /**
     * Obtiene información completa de un miembro por su ID
     * Incluye email desde msvc-security y membership activa
     * @param memberId ID del miembro
     * @return Información del miembro con email y membership
     */
    @GetMapping("/member/{memberId}/info")
    MemberInfoDTO getMemberInfo(@PathVariable("memberId") UUID memberId);
}
