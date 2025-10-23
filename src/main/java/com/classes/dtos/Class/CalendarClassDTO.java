package com.classes.dtos.Class;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalendarClassDTO {
    private UUID id;
    private String className;
    private String trainerName;
    
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate classDate;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;
    
    private String schedule; // "08:00 - 09:00"
    private String locationName;
    private int currentStudents;
    private int maxCapacity;
    private String action; // "Reservar", "Lista de espera", "Llena"
    private String status; // "PROGRAMADA", "EN_PROCESO", "COMPLETADA", "CANCELADA"
}
