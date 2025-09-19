package com.classes.mappers;


import com.classes.dtos.LocationDTO;
import com.classes.config.MapStructConfig;
import com.classes.entities.LocationEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = MapStructConfig.class)
public interface LocationMapper {

    LocationDTO toDto(LocationEntity locationEntity);
    LocationEntity toEntity(LocationDTO locationDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(LocationDTO dto, @MappingTarget LocationEntity entity);


}
