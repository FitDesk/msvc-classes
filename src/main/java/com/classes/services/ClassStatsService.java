package com.classes.services;

import com.classes.dtos.Class.CalendarClassDTO;
import com.classes.dtos.Class.ClassDetailResponse;
import com.classes.dtos.Class.ClassWithStatsResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


public interface ClassStatsService {


    List<ClassWithStatsResponse> getClassesWithStatsByTrainer(UUID trainerId);

    ClassDetailResponse getClassDetail(UUID classId);

    List<CalendarClassDTO> getClassesForCalendar(LocalDate startDate, LocalDate endDate, String status, UUID locationId);
    
    List<CalendarClassDTO> getUpcomingClasses(String status, UUID locationId);
    
    List<CalendarClassDTO> getClassesForCalendar(LocalDate startDate, LocalDate endDate);
    
    List<CalendarClassDTO> getUpcomingClasses();
}
