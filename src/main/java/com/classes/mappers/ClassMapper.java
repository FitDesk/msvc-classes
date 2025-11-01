package com.classes.mappers;

import com.classes.config.MapStructConfig;
import com.classes.dtos.Class.ClassRequest;
import com.classes.dtos.Class.ClassResponse;
import com.classes.entities.ClassEntity;

import org.mapstruct.*;

import java.util.List;


@Mapper(config = MapStructConfig.class)
public interface ClassMapper {

    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "trainer", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    @Mapping(target = "audit", ignore = true)
    @Mapping(target = "status", ignore = true) 
    ClassEntity toEntity(ClassRequest request);

    
    @Mapping(target = "locationName", source = "location.name")
    @Mapping(target = "trainerName", expression = "java(entity.getTrainer().getFirstName() + \" \" + entity.getTrainer().getLastName())")
    @Mapping(target = "schedule", expression = "java(entity.getStartTime() + \" - \" + entity.getEndTime())")
    @Mapping(target = "status", expression = "java(entity.getStatus().name())")
    ClassResponse toResponse(ClassEntity entity);

    
    List<ClassResponse> toResponseList(List<ClassEntity> entities);

    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainer", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    @Mapping(target = "audit", ignore = true)
    @Mapping(target = "status", ignore = true) 
    void updateFromRequest(ClassRequest request, @MappingTarget ClassEntity entity);
}
