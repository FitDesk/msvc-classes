package com.classes.dtos.Class;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MonthlyCalendarDTO {
    private int year;
    private int month;
    private String monthName; // "Septiembre"
    private LocalDate firstDayOfMonth;
    private LocalDate lastDayOfMonth;
    
    // Mapa de fecha -> lista de clases en esa fecha
    private Map<LocalDate, List<CalendarClassDTO>> classesByDate;
    
    // Total de clases en el mes
    private int totalClasses;
    
    // Días que tienen clases (para marcar en el calendario)
    private List<LocalDate> daysWithClasses;
}
