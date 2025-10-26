package com.classes.dtos.Class;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateAttendanceRequest {
    private String attendanceStatus; // "PRESENTE", "AUSENTE", "TARDE", "JUSTIFICADO"
}
