package com.classes.controllers;

import com.classes.DTOs.LocationDTO;
import com.classes.entity.Location;
import com.classes.services.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    public ResponseEntity<LocationDTO> create(@RequestBody LocationDTO dto) {
        LocationDTO created = locationService.create(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    public ResponseEntity<LocationDTO> update(@PathVariable Long id, @RequestBody LocationDTO dto) {
        LocationDTO updated = locationService.update(id, dto);
        return ResponseEntity.ok(updated);
    }
    @GetMapping
    public ResponseEntity<List<Location>> findAll() {
        return ResponseEntity.ok(locationService.FindAll());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Location> findById(@PathVariable Long id) {
       return ResponseEntity.ok(locationService.findById(id));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        locationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
