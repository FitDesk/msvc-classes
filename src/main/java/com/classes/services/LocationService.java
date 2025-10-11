package com.classes.services;

import com.classes.dtos.Location.LocationRequest;
import com.classes.dtos.Location.LocationResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface LocationService {

    LocationResponse create(LocationRequest request);

    LocationResponse update(UUID id, LocationRequest request);

    void delete(UUID id);

    LocationResponse findById(UUID id);

    // Paginación + filtros
    Page<LocationResponse> findAll(int page, int size, String search, Boolean active);
}