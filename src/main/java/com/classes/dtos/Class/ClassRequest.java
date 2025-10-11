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
public class ClassRequest {
    private String className;
    private UUID locationId;
    private UUID trainerId;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate classDate;
    private int duration;
    private int maxCapacity;
    private LocalTime startTime;
    private boolean active;
    private String description;
}