package com.classes.dtos.Dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentTrendDTO {
    private String week; 
    private int activeStudents;
    private int inactiveStudents;
    private String label;
}
