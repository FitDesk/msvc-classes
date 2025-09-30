package com.classes.controllers;

import com.classes.dtos.ClassDTO;
import com.classes.services.ClassService;
import org.springframework.web.bind.annotation.PostMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class ClassController {

    private final ClassService classService;


    @PostMapping
    public ResponseEntity<ClassDTO> createClass(@RequestBody ClassDTO dto) {
        ClassDTO created = classService.createClass(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ClassDTO>> findAllClasses() {
        List<ClassDTO> list = classService.findAll();
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClassDTO> updateClass(@PathVariable UUID id, @RequestBody ClassDTO dto) {
        ClassDTO updated = classService.updateClass(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClass(@PathVariable UUID id) {
        classService.deleteClass(id);
        return ResponseEntity.ok("Clase eliminada correctamente");
    }
}
