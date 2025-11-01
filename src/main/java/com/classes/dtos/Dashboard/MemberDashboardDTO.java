package com.classes.dtos.Dashboard;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class MemberDashboardDTO {

    private boolean inClass; 
    private int remainingClasses; 
    private String nextClassName;
    private String nextClassTime;
    private int consecutiveDays;
    private List<WeeklyActivityDTO> weeklyActivity;
    private List<UpcomingClassDTO> upcomingClasses;
}