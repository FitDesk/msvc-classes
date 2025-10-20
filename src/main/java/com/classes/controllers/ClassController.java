package com.classes.controllers;

import com.classes.dtos.Class.ClassRequest;
import com.classes.dtos.Class.ClassResponse;
import com.classes.services.ClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/classes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin - Clases", description = "Gestión administrativa de clases (CRUD)")
public class ClassController {

    private final ClassService classService;
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ClassResponse>> getAllClasses() {
        log.info("Listando todas las clases");
        List<ClassResponse> classes = classService.findAll();
        return ResponseEntity.ok(classes);
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClassResponse> createClass(@RequestBody ClassRequest request) {
        log.info("Admin creando nueva clase: {}", request.getClassName());
        ClassResponse created = classService.createClass(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClassResponse> updateClass(
            @PathVariable UUID id,
            @RequestBody ClassRequest request) {
        log.info("Admin actualizando clase: {}", id);
        ClassResponse updated = classService.updateClass(id, request);
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteClass(@PathVariable UUID id) {
        log.info("🗑Admin eliminando clase: {}", id);
        classService.deleteClass(id);
        return ResponseEntity.ok("Clase eliminada correctamente");
    }
}
