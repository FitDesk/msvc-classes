package com.classes.services.Impl;

import com.classes.DTOs.LocationDTO;
import com.classes.entity.Location;
import com.classes.mappers.LocationMapper;
import com.classes.repository.LocationRepository;
import com.classes.services.LocationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository repository;
    private final LocationMapper mapper;

    @Override
    public LocationDTO create(LocationDTO dto) {
        Location location = mapper.toEntity(dto);
        return mapper.toDto(repository.save(location));
    }
    @Override
    public LocationDTO update(Long id, LocationDTO dto) {
        Location location= findById(id);
        mapper.updateFromDto(dto, location);
        Location updated = repository.save(location);
        return mapper.toDto(updated);
    }
    @Override
    public Location findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Location not found"));
    }
    @Override
    public List<Location> FindAll() {
        return repository.findAll();
    }
    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

}
