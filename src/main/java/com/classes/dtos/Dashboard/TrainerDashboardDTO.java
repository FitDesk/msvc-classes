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
    private int totalStudents;
    private double averageAttendance; // Porcentaje promedio
    private int classesThisMonth;
    private double attendanceChange; // Cambio porcentual respecto al mes anterior
    private List<StudentTrendDTO> studentTrends; // Tendencia semanal de estudiantes
}
