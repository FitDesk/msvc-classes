package com.classes.services.Impl;

import com.classes.dtos.LocationDTO;
import com.classes.entities.LocationEntity;
import com.classes.mappers.LocationMapper;
import com.classes.repositories.LocationRepository;
import com.classes.services.LocationService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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
    public LocationDTO update(UUID id, LocationDTO dto) {
        LocationEntity locationEntity = findById(id);
        mapper.updateFromDto(dto, locationEntity);
        LocationEntity updated = repository.save(locationEntity);
        return mapper.toDto(updated);
    }


    @Override
    @Transactional(readOnly = true)
    public LocationEntity findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found"));
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<LocationDTO> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<LocationEntity> pageEntity = repository.findAll(pageable);
        return pageEntity.map(mapper::toDto);
    }

}
