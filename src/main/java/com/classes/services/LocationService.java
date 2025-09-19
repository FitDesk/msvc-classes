package com.classes.services;

import com.classes.dtos.LocationDTO;
import com.classes.entities.LocationEntity;

import java.util.List;

public interface LocationService {

    LocationDTO create(LocationDTO dto);
    LocationDTO update(Long id, LocationDTO dto);
    void delete(Long id);
    LocationEntity findById(Long id);
    List<LocationEntity> FindAll();
}
