package com.classes.services.Impl;

import com.classes.dtos.Location.LocationRequest;
import com.classes.dtos.Location.LocationResponse;
import com.classes.entities.LocationEntity;
import com.classes.mappers.LocationMapper;
import com.classes.repositories.ClassRepository;
import com.classes.repositories.LocationRepository;
import com.classes.services.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository repository;
    private final LocationMapper mapper;
    private final ClassRepository classRepository;

    @Override
    @Transactional
    public LocationResponse create(LocationRequest request) {
        LocationEntity entity = mapper.toEntity(request);
        LocationEntity saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public LocationResponse update(UUID id, LocationRequest request) {
        LocationEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found"));
        mapper.updateFromRequest(request, entity);
        LocationEntity updated = repository.save(entity);
        return mapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        boolean hasClasses = classRepository.findFirstByLocationId(id).isPresent();
        if (hasClasses) {
            throw new IllegalArgumentException(
                    "No se puede eliminar porque hay clases asociadas con este ID"
            );
        }
        repository.deleteById(id);
    }

    @Override
    public LocationResponse findById(UUID id) {
        LocationEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found"));
        return mapper.toResponse(entity);
    }

    @Override
    public Page<LocationResponse> findAll(int page, int size, String search, Boolean active) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<LocationEntity> pageResult;

        if (search != null && !search.isEmpty() && active != null) {
            pageResult = repository.findByNameContainingIgnoreCaseAndActive(search, active, pageable);
        } else if (search != null && !search.isEmpty()) {
            pageResult = repository.findByNameContainingIgnoreCase(search, pageable);
        } else if (active != null) {
            pageResult = active ? repository.findByActiveTrue(pageable)
                    : repository.findByActiveFalse(pageable);
        } else {
            pageResult = repository.findAll(pageable);
        }

        return pageResult.map(mapper::toResponse);
    }
}

