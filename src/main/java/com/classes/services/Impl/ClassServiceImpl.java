package com.classes.services.Impl;

import com.classes.dtos.ClassDTO;
import com.classes.entities.ClassEntity;
import com.classes.mappers.ClassMapper;
import com.classes.repositories.ClassRepository;
import com.classes.repositories.LocationRepository;
import com.classes.repositories.TrainerRepository;
import com.classes.services.ClassService;
import lombok.AllArgsConstructor;
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
    public ClassDTO createClass(ClassDTO dto) {
        validateTrainerAndLocation(dto);
        ClassEntity entity = classMapper.toEntity(dto);
        ClassEntity saved = repository.save(entity);
        return classMapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ClassDTO> findAll() {
        List<ClassEntity> entities = repository.findAll();
        return classMapper.toDTOList(entities);
    }

    @Transactional
    @Override
    public ClassDTO updateClass(UUID id, ClassDTO dto) {
        ClassEntity existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La clase con ID " + id + " no existe"));
        validateTrainerAndLocation(dto);
        classMapper.updateFromDto(dto, existing);
        ClassEntity updated = repository.save(existing);
        return classMapper.toDTO(updated);
    }

    @Transactional
    @Override
    public void deleteClass(UUID id) {

    }

    private void validateTrainerAndLocation(ClassDTO dto) {
        if (!trainerRepository.existsById(dto.getTrainerId())) {
            throw new IllegalArgumentException("El trainer con ID " + dto.getTrainerId() + " no existe");
        }
        if (!locationRepository.existsById(dto.getLocationId())) {
            throw new IllegalArgumentException("La ubicación con ID " + dto.getLocationId() + " no existe");
        }
    }
}
