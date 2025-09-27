package com.classes.mappers;

import com.classes.config.MapStructConfig;
import com.classes.dtos.TrainerDTO;
import com.classes.entities.TrainerEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface TrainerMapper {
    TrainerEntity toEntity(TrainerDTO dto);
    TrainerDTO toDTO(TrainerEntity trainer);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateTrainerFromDTO(TrainerDTO dto, @MappingTarget TrainerEntity trainer);
    List<TrainerDTO> toDTOList(List<TrainerEntity> trainers);
}
