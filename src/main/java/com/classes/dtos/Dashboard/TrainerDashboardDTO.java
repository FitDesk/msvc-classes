package com.classes.dtos.Dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainerDashboardDTO {
    private int totalClasses; 
    private int completedClasses;
    private int totalStudents;
    private double averageAttendance;
    private int upcomingClasses;
    private int classesThisMonth;
    private double attendanceChange;
    private List<StudentTrendDTO> studentTrends;
}
