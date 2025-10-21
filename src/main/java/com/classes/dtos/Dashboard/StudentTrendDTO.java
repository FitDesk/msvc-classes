package com.classes.dtos.Dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentTrendDTO {
    private String week; // "Sem 1", "Sem 2", etc.
    private int activeStudents;
    private int inactiveStudents;
    private String label; // Para el tooltip del gráfico
}
