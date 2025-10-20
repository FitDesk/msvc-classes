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

    /**
     * Obtiene todas las clases con estadísticas de un trainer
     */
    List<ClassWithStatsResponse> getClassesWithStatsByTrainer(UUID trainerId);

    /**
     * Obtiene el detalle completo de una clase con la lista de estudiantes
     */
    ClassDetailResponse getClassDetail(UUID classId);

    /**
     * Obtiene las clases para el calendario en un rango de fechas
     */
    List<CalendarClassDTO> getClassesForCalendar(LocalDate startDate, LocalDate endDate);

    /**
     * Obtiene las próximas clases futuras
     */
    List<CalendarClassDTO> getUpcomingClasses();
}
