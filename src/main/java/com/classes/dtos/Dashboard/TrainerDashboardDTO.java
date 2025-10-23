package com.classes.dtos.Dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainerDashboardDTO {
    private int totalClasses; // Total de clases asignadas al trainer
    private int completedClasses; // Clases con status COMPLETADA
    private int totalStudents; // Total de estudiantes únicos en todas las clases
    private double averageAttendance; // Porcentaje promedio
    private int upcomingClasses; // Próximas clases (futuras)
    private int classesThisMonth;
    private double attendanceChange; // Cambio porcentual respecto al mes anterior
    private List<StudentTrendDTO> studentTrends; // Tendencia semanal de estudiantes
}
