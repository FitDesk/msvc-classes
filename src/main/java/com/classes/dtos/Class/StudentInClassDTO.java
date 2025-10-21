package com.classes.dtos.Class;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentInClassDTO {
    private UUID memberId;
    private String name;
    private String email;
    private String avatarInitials; // Para mostrar iniciales en avatar
    private String status; // "Activo", "Inactivo", "Suspendido"
    private String membershipType; // "Mensual", "Anual", "Premium"
    private double attendancePercentage; // Porcentaje de asistencia
    private int totalClasses; // Total de clases del estudiante
    
    @JsonFormat(pattern = "dd MMM yyyy")
    private LocalDateTime lastAccess;
    
    private UUID reservationId;
}
