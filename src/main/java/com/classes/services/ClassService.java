package com.classes.services;

import com.classes.dtos.Class.*;
import org.springframework.data.domain.Page;
import com.classes.dtos.Class.ClassRequest;
import com.classes.dtos.Class.ClassResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ClassService {


    ClassResponse createClass(ClassRequest request);

    List<ClassResponse> findAll();

    Page<ClassResponse> findAllPaginated(int page, int size, String search);

    ClassResponse updateClass(UUID id, ClassRequest request);

    void deleteClass(UUID id);


    List<ClassWithStatsResponse> getClassesWithStatsByTrainer(UUID trainerId);

    ClassDetailResponse getClassDetail(UUID classId);

    List<CalendarClassDTO> getClassesForCalendar(LocalDate startDate, LocalDate endDate);

    List<CalendarClassDTO> getUpcomingClasses();
}
