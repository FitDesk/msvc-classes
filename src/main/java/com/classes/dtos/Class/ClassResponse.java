package com.classes.dtos.Class;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassResponse {
    private UUID id;
    private String className;
    private String locationName;
    private String trainerName;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate classDate;
    private int duration;
    private int maxCapacity;
    private String schedule;
    private boolean active;
    private String description;
    private String status;
}