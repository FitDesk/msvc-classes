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
    private UUID reservationId;
    private String name;
    private String email;
    private String avatarInitials; // Para mostrar iniciales en avatar
    private String profileImageUrl; // URL de imagen de perfil
    private String status; // "Activo", "Inactivo", "Suspendido"
    private String membershipType; // "Mensual", "Anual", "Premium"
    
    // Campos de asistencia
    private String attendanceStatus; // "PRESENTE", "AUSENTE", "TARDE", "JUSTIFICADO"
    
    @JsonFormat(pattern = "HH:mm")
    private LocalDateTime checkInTime; // Hora de llegada (ej: "22:13")
    
    // Estadísticas del estudiante
    private double attendancePercentage; // Porcentaje de asistencia
    private int totalClasses; // Total de clases del estudiante
    
    @JsonFormat(pattern = "dd MMM yyyy")
    private LocalDateTime lastAccess;
}
