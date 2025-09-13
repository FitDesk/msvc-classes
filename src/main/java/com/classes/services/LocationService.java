package com.classes.services;

import com.classes.DTOs.LocationDTO;
import com.classes.entity.Location;

import java.util.List;
import java.util.Optional;

public interface LocationService {

    LocationDTO create(LocationDTO dto);
    LocationDTO update(Long id, LocationDTO dto);
    void delete(Long id);
    Location findById(Long id);
    List<Location> FindAll();
}
