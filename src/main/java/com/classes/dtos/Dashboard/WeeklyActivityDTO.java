package com.classes.dtos.Dashboard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeeklyActivityDTO {
    private String day;
    private int sessions;
}