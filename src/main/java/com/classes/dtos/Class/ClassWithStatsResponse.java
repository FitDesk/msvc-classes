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
public class ClassWithStatsResponse {
    private UUID id;
    private String className;
    private String description;
    private int currentStudents;
    private int maxCapacity;
    private String trainerName;
    private String locationName;
    
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate classDate;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    private String schedule;
    private double averageAttendance;
    private boolean active;
    private String status;
}
