package com.classes.services;

import com.classes.dtos.Class.*;
import org.springframework.data.domain.Page;
import com.classes.dtos.Class.ClassRequest;
import com.classes.dtos.Class.ClassResponse;
import com.classes.dtos.Class.MonthlyCalendarDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ClassService {


    ClassResponse createClass(ClassRequest request);

    List<ClassResponse> findAll();

    Page<ClassResponse> findAllPaginated(int page, int size, String search);

    ClassResponse updateClass(UUID id, ClassRequest request);

    void deleteClass(UUID id);
    
    MonthlyCalendarDTO getMonthlyCalendar(int year, int month);
}
