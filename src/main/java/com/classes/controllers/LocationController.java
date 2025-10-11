package com.classes.controllers;

import com.classes.annotations.AdminOrTrainerAccess;
import com.classes.dtos.Location.LocationRequest;
import com.classes.dtos.Location.LocationResponse;
import com.classes.services.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
@AdminOrTrainerAccess
public class LocationController {

    private final LocationService locationService;

    @PreAuthorize("@authorizationServiceImpl.canAccessResource(#id,authentication)")
    @PostMapping
    public ResponseEntity<LocationResponse> create(@RequestBody LocationRequest request) {
        LocationResponse created = locationService.create(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PreAuthorize("@authorizationServiceImpl.canAccessResource(#id,authentication)")
    @GetMapping
    public ResponseEntity<Page<LocationResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean active
    ) {
        Page<LocationResponse> locations = locationService.findAll(page, size, search, active);
        return ResponseEntity.ok(locations);
    }
    @PreAuthorize("@authorizationServiceImpl.canAccessResource(#id,authentication)")
    @GetMapping("/{id}")
    public ResponseEntity<LocationResponse> findById(@PathVariable UUID id) {
        LocationResponse location = locationService.findById(id);
        return ResponseEntity.ok(location);
    }

    @PreAuthorize("@authorizationServiceImpl.canAccessResource(#id,authentication)")
    @PutMapping("/{id}")
    public ResponseEntity<LocationResponse> update(
            @PathVariable UUID id,
            @RequestBody LocationRequest request
    ) {
        LocationResponse updated = locationService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("@authorizationServiceImpl.canAccessResource(#id,authentication)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        locationService.delete(id); // Validación interna
        return ResponseEntity.noContent().build();
    }
}
