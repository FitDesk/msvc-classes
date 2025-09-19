package com.classes.services.Impl;

import com.classes.dtos.LocationDTO;
import com.classes.entities.LocationEntity;
import com.classes.mappers.LocationMapper;
import com.classes.repositories.LocationRepository;
import com.classes.services.LocationService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository repository;
    private final LocationMapper mapper;

    @Override
    @Transactional
    public LocationDTO create(LocationDTO dto) {
        LocationEntity locationEntity = mapper.toEntity(dto);
        return mapper.toDto(repository.save(locationEntity));
    }

    @Override
    @Transactional
    public LocationDTO update(Long id, LocationDTO dto) {
        LocationEntity locationEntity = findById(id);
        mapper.updateFromDto(dto, locationEntity);
        LocationEntity updated = repository.save(locationEntity);
        return mapper.toDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public LocationEntity findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Location not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationEntity> FindAll() {
        return repository.findAll();
    }

    @Transactional
    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

}
