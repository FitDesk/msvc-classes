package com.classes.mappers;

import com.classes.config.MapStructConfig;
import com.classes.dtos.Trainer.TrainerRequestDTO;
import com.classes.dtos.Trainer.TrainerResponseDTO;
import com.classes.entities.TrainerEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface TrainerMapper {
    TrainerEntity toEntity(TrainerRequestDTO dto);

    TrainerResponseDTO toResponseDTO(TrainerEntity trainer);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateTrainerFromDTO(TrainerRequestDTO dto, @MappingTarget TrainerEntity trainer);

    List<TrainerResponseDTO> toResponseDTOList(List<TrainerEntity> trainers);
}
