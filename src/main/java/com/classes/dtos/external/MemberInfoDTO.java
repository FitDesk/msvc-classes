package com.classes.dtos.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para recibir información del microservicio de Members
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberInfoDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String status; // "ACTIVO", "INACTIVO", "SUSPENDIDO"
    private String membershipType; // "MENSUAL", "ANUAL", "PREMIUM"
    private LocalDateTime lastAccess;
}
