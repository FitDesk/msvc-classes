package com.classes.mappers;


import com.classes.DTOs.LocationDTO;
import com.classes.config.MapStructConfig;
import com.classes.entity.Location;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = MapStructConfig.class)
public interface LocationMapper {

    LocationDTO toDto(Location location);
    Location toEntity(LocationDTO locationDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(LocationDTO dto, @MappingTarget Location entity);


}
