package com.classes.dtos.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para recibir información del microservicio de Members
 * Mapea la respuesta de msvc-members con los nombres correctos de campos
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberInfoDTO {
    @JsonProperty("userId")  // msvc-members usa "userId"
    private UUID id;
    
    private String firstName;
    private String lastName;
    private String email;
    private String dni;
    private String phone;
    private String initials;
    private String profileImageUrl;
    
    private String status; // "ACTIVO", "INACTIVO", "SUSPENDIDO"
    
    @JsonProperty("membership")  // msvc-members usa "membership"
    private MembershipDTO membershipType;
    
    private LocalDateTime lastAccess;
}
