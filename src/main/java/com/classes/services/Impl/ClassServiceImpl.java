package com.classes.services.Impl;

import com.classes.dtos.Class.ClassRequest;
import com.classes.dtos.Class.ClassResponse;
import com.classes.entities.ClassEntity;
import com.classes.entities.LocationEntity;
import com.classes.entities.TrainerEntity;
import com.classes.mappers.ClassMapper;
import com.classes.repositories.ClassRepository;
import com.classes.repositories.LocationRepository;
import com.classes.repositories.TrainerRepository;
import com.classes.services.ClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {

    private final ClassRepository repository;
    private final ClassMapper classMapper;
    private final TrainerRepository trainerRepository;
    private final LocationRepository locationRepository;


    @Transactional
    @Override
    public ClassResponse createClass(ClassRequest request) {
        validateTrainerAndLocation(request);
        ClassEntity entity = classMapper.toEntity(request);
        TrainerEntity trainer = trainerRepository.findById(request.getTrainerId())
                .orElseThrow(() -> new IllegalArgumentException("El trainer con ID " + request.getTrainerId() + " no existe"));
        LocationEntity location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new IllegalArgumentException("La ubicación con ID " + request.getLocationId() + " no existe"));
        entity.setTrainer(trainer);
        entity.setLocation(location);
        ClassEntity saved = repository.save(entity);
        return classMapper.toResponse(saved);
    }

    @Transactional
    @Override
    public List<ClassResponse> findAll() {
        return classMapper.toResponseList(repository.findAll());
    }

    @Transactional
    @Override
    public ClassResponse updateClass(UUID id, ClassRequest request) {

        ClassEntity existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La clase con ID " + id + " no existe"));


        if (request.getTrainerId() != null) {
            TrainerEntity trainer = trainerRepository.findById(request.getTrainerId())
                    .orElseThrow(() -> new IllegalArgumentException("El trainer con ID " + request.getTrainerId() + " no existe"));
            existing.setTrainer(trainer);
        }


        if (request.getLocationId() != null) {
            LocationEntity location = locationRepository.findById(request.getLocationId())
                    .orElseThrow(() -> new IllegalArgumentException("La ubicación con ID " + request.getLocationId() + " no existe"));
            existing.setLocation(location);
        }
        classMapper.updateFromRequest(request, existing);
        ClassEntity updated = repository.save(existing);
        return classMapper.toResponse(updated);
    }

    @Transactional
    @Override
    public void deleteClass(UUID id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("La clase con ID " + id + " no existe");
        }
        repository.deleteById(id);
    }

    private void validateTrainerAndLocation(ClassRequest request) {
        if (!trainerRepository.existsById(request.getTrainerId())) {
            throw new IllegalArgumentException("El trainer con ID " + request.getTrainerId() + " no existe");
        }
        if (!locationRepository.existsById(request.getLocationId())) {
            throw new IllegalArgumentException("La ubicación con ID " + request.getLocationId() + " no existe");
        }
    }
}
