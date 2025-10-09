package com.classes.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class ClassDTO {
    private UUID id;
    private String className;
    private UUID locationId;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate classDate;
    private int duration;
    private UUID trainerId;
    private int maxCapacity;
    private LocalTime startTime;
    private boolean active;
    private String description;
}
