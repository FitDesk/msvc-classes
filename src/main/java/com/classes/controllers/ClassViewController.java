package com.classes.controllers;

import com.classes.dtos.Class.*;
import com.classes.dtos.Class.MonthlyCalendarDTO;
import com.classes.services.AuthorizationService;
import com.classes.services.ClassService;
import com.classes.services.ClassStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/stadistic")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Clases - Vistas", description = "Consultas y vistas de clases")
public class ClassViewController {

    private final ClassService classService;
    private final ClassStatsService classStatsService;
    private final AuthorizationService authorizationService;

    @Operation(summary = "Listar todas las clases", description = "Todos los roles pueden ver la lista")
    @GetMapping
    public ResponseEntity<List<ClassResponse>> findAllClasses() {
        log.info("Consultando todas las clases");
        List<ClassResponse> list = classService.findAll();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Obtener mis clases con estadísticas", description = "Vista para trainers con métricas")
    @GetMapping("/my-classes/stats")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<List<ClassWithStatsResponse>> getMyClassesWithStats(Authentication authentication) {
        UUID trainerId = authorizationService.getUserId(authentication);
        log.info("Trainer {} consultando sus clases con estadísticas", trainerId);
        List<ClassWithStatsResponse> classes = classStatsService.getClassesWithStatsByTrainer(trainerId);
        return ResponseEntity.ok(classes);
    }

    @Operation(summary = "Ver detalle de clase con estudiantes", description = "Vista detallada para trainers")
    @GetMapping("/{id}/detail")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<ClassDetailResponse> getClassDetail(@PathVariable UUID id) {
        log.info("Consultando detalle de la clase {}", id);
        ClassDetailResponse detail = classStatsService.getClassDetail(id);
        return ResponseEntity.ok(detail);
    }

    @Operation(summary = "Ver calendario de clases", description = "Vista de calendario para todos")
    @GetMapping("/calendar")
    @PreAuthorize("hasRole('USER') or hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<List<CalendarClassDTO>> getClassesForCalendar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("📅 Consultando clases para calendario");
        
        if (startDate != null && endDate != null) {
            List<CalendarClassDTO> classes = classStatsService.getClassesForCalendar(startDate, endDate);
            return ResponseEntity.ok(classes);
        } else {
            List<CalendarClassDTO> classes = classStatsService.getUpcomingClasses();
            return ResponseEntity.ok(classes);
        }
    }

    @Operation(summary = "Ver próximas clases", description = "Lista de clases futuras")
    @GetMapping("/upcoming")
    @PreAuthorize("hasRole('USER') or hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<List<CalendarClassDTO>> getUpcomingClasses() {
        log.info("Consultando próximas clases");
        List<CalendarClassDTO> classes = classStatsService.getUpcomingClasses();
        return ResponseEntity.ok(classes);
    }

    @Operation(summary = "Ver calendario mensual", description = "Vista de calendario por mes con clases agrupadas por fecha")
    @GetMapping("/calendar/monthly")
    @PreAuthorize("hasRole('USER') or hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<MonthlyCalendarDTO> getMonthlyCalendar(
            @RequestParam int year,
            @RequestParam int month) {
        log.info("📅 Consultando calendario mensual para {}/{}", month, year);
        MonthlyCalendarDTO calendar = classService.getMonthlyCalendar(year, month);
        return ResponseEntity.ok(calendar);
    }
}
