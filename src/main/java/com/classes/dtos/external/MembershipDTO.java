package com.classes.dtos.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para recibir información de la membresía desde msvc-members
 * Mapea exactamente los campos que devuelve MembershipDto de msvc-members
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MembershipDTO {
    @JsonProperty("planName")
    private String type; // Mapea planName a type para mantener compatibilidad
    
    private String status; // "ACTIVE", "EXPIRED", "CANCELLED", "SUSPENDED"
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer daysRemaining;
    private boolean isActive;
    private boolean isExpired;
}
