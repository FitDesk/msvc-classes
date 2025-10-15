package com.classes.dtos.Dashboard;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class MemberDashboardDTO {

    private boolean inClass; // true si está actualmente en una clase activa
    private int remainingClasses; // cuántas clases le quedan del plan
    private String nextClassName; // nombre de la próxima clase
    private String nextClassTime; // hora de la próxima clase
    private int consecutiveDays; // días consecutivos asistidos
    private List<WeeklyActivityDTO> weeklyActivity; // gráfico semanal
    private List<UpcomingClassDTO> upcomingClasses; // próximas clases
}