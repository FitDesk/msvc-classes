package com.classes.mappers;

import com.classes.config.MapStructConfig;
import com.classes.dtos.ClassDTO;
import com.classes.dtos.LocationDTO;
import com.classes.entities.ClassEntity;
import com.classes.entities.LocationEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface ClassMapper {
    ClassEntity toEntity(ClassDTO dto);
    ClassDTO toDTO(ClassEntity entity);
    List<ClassDTO> toDTOList(List<ClassEntity> entities);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(ClassDTO dto, @MappingTarget ClassEntity entity);


}
