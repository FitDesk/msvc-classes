package com.classes.dtos.Dashboard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeeklyActivityDTO {
    private String day; // Lunes, Martes, etc.
    private int sessions; // cantidad de clases asistidas ese día
}