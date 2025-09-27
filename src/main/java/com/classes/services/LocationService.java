package com.classes.services;

import com.classes.dtos.LocationDTO;
import com.classes.entities.LocationEntity;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface LocationService {

    LocationDTO create(LocationDTO dto);
    LocationDTO update(UUID id, LocationDTO dto);
    void delete(UUID id);
    LocationEntity findById(UUID id);
    Page<LocationDTO> findAll(int page, int size);
}
