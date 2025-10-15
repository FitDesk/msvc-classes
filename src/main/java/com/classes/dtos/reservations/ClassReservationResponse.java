package com.classes.dtos.reservations;

import java.util.UUID;

import lombok.*;
import lombok.Data;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClassReservationResponse {
    private UUID reservationId;
    private UUID classId;
    private String className;
    private String trainerName;
    private String schedule;
    private String locationName;
    private String capacity;
    private String action;
    private boolean alreadyReserved;
    private boolean completed;
}
