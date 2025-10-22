package com.classes.controllers;

import com.classes.dtos.Class.ClassRequest;
import com.classes.dtos.Class.ClassResponse;
import com.classes.dtos.Class.MonthlyCalendarDTO;
import com.classes.services.ClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/classes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin - Clases", description = "Gestión administrativa de clases (CRUD)")
public class ClassController {

    private final ClassService classService;

    @PostMapping
    @PreAuthorize("@authorizationServiceImpl.canAccessResource(#id, authentication)")
    public ResponseEntity<ClassResponse> createClass(@RequestBody ClassRequest request) {
        log.info("Admin creando nueva clase: {}", request.getClassName());
        ClassResponse created = classService.createClass(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @PreAuthorize("@authorizationServiceImpl.canAccessResource(#id, authentication)")
    public ResponseEntity<List<ClassResponse>> findAllClasses() {
        List<ClassResponse> list = classService.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<ClassResponse>> findAllClassesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        Page<ClassResponse> result = classService.findAllPaginated(page, size, search);
        return ResponseEntity.ok(result);
    }


    @PutMapping("/{id}")
    @PreAuthorize("@authorizationServiceImpl.canAccessResource(#id, authentication)")
    public ResponseEntity<ClassResponse> updateClass(@PathVariable UUID id, @RequestBody ClassRequest request) {
        ClassResponse updated = classService.updateClass(id, request);
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("@authorizationServiceImpl.canAccessResource(#id, authentication)")
    public ResponseEntity<String> deleteClass(@PathVariable UUID id) {
        log.info("🗑Admin eliminando clase: {}", id);
        classService.deleteClass(id);
        return ResponseEntity.ok("Clase eliminada correctamente");
    }

    @Operation(summary = "Ver calendario mensual", description = "Vista de calendario por mes con clases agrupadas por fecha")
    @GetMapping("/calendar/monthly")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MonthlyCalendarDTO> getMonthlyCalendar(
            @RequestParam int year,
            @RequestParam int month) {
        log.info("📅 Admin consultando calendario mensual para {}/{}", month, year);
        MonthlyCalendarDTO calendar = classService.getMonthlyCalendar(year, month);
        return ResponseEntity.ok(calendar);
    }
}
