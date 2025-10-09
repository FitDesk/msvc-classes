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
    ClassEntity toEntity(ClassRequest request);
    ClassResponse toResponse(ClassEntity entity);

    // Lista de respuestas
    List<ClassResponse> toResponseList(List<ClassEntity> entities);

    // Actualizar entidad desde el request (solo los campos no nulos)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromRequest(ClassRequest request, @MappingTarget ClassEntity entity);
}
