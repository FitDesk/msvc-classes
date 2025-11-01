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
    private String monthName;
    private LocalDate firstDayOfMonth;
    private LocalDate lastDayOfMonth;
    
    private Map<LocalDate, List<CalendarClassDTO>> classesByDate;
    
    private int totalClasses;
    
    private List<LocalDate> daysWithClasses;
}
