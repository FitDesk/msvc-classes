package com.classes.dtos.Dashboard;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpcomingClassDTO {
    private String className;
    private String trainerName;
    private String schedule;
    private String location;
}