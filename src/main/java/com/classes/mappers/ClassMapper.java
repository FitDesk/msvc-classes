package com.classes.mappers;

import com.classes.config.MapStructConfig;
import com.classes.dtos.Class.ClassRequest;

import com.classes.dtos.Class.ClassResponse;
import com.classes.entities.ClassEntity;

import org.mapstruct.*;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface ClassMapper {

    // Crear entidad desde el request

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "location", ignore = true) // se setean en el servicio
    @Mapping(target = "trainer", ignore = true)
    ClassEntity toEntity(ClassRequest request);

    @Mapping(target = "locationName", source = "location.name")
    @Mapping(target = "trainerName", source = "trainer.firstName")
    ClassResponse toResponse(ClassEntity entity);

    // Lista de respuestas
    List<ClassResponse> toResponseList(List<ClassEntity> entities);

    // Actualizar entidad desde el request (solo los campos no nulos)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromRequest(ClassRequest request, @MappingTarget ClassEntity entity);
}
