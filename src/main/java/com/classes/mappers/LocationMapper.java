package com.classes.mappers;


import com.classes.dtos.Location.LocationRequest;
import com.classes.dtos.Location.LocationResponse;
import com.classes.config.MapStructConfig;
import com.classes.entities.LocationEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = MapStructConfig.class)
public interface LocationMapper {

    LocationResponse toResponse(LocationEntity locationEntity);

    // Request → Entity
    LocationEntity toEntity(LocationRequest locationRequest);

    // Actualización parcial: Request → Entity
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromRequest(LocationRequest request, @MappingTarget LocationEntity entity);


}
