package com.classes.mappers;

import com.classes.config.MapStructConfig;
import com.classes.dtos.Class.ClassRequest;
import com.classes.dtos.Class.ClassResponse;
import com.classes.entities.ClassEntity;

import org.mapstruct.*;

import java.util.List;

/**
 * Mapper básico para operaciones CRUD de clases
 * Para estadísticas y vistas especiales, usar ClassStatsMapper
 */
@Mapper(config = MapStructConfig.class)
public interface ClassMapper {

    /**
     * Crear entidad desde el request (para INSERT)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "location", ignore = true) // se setean en el servicio
    @Mapping(target = "trainer", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    @Mapping(target = "audit", ignore = true)
    ClassEntity toEntity(ClassRequest request);

    /**
     * Convertir entidad a response básico
     */
    @Mapping(target = "locationName", source = "location.name")
    @Mapping(target = "trainerName", expression = "java(entity.getTrainer().getFirstName() + \" \" + entity.getTrainer().getLastName())")
    @Mapping(target = "schedule", expression = "java(entity.getStartTime() + \" - \" + entity.getEndTime())")
    ClassResponse toResponse(ClassEntity entity);

    /**
     * Lista de respuestas
     */
    List<ClassResponse> toResponseList(List<ClassEntity> entities);

    /**
     * Actualizar entidad desde el request (para UPDATE - solo los campos no nulos)
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainer", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    @Mapping(target = "audit", ignore = true)
    void updateFromRequest(ClassRequest request, @MappingTarget ClassEntity entity);
}
