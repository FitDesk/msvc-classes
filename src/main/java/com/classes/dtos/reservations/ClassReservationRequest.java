package com.classes.dtos.reservations;

import java.util.UUID;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassReservationRequest {
    private UUID classId;
}