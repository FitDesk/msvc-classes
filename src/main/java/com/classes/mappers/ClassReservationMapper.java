package com.classes.mappers;


import com.classes.config.MapStructConfig;

import com.classes.dtos.reservations.ClassReservationRequest;
import com.classes.dtos.reservations.ClassReservationResponse;
import com.classes.entities.ClassReservation;
import org.mapstruct.*;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface ClassReservationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "classEntity", ignore = true) // Service asigna la clase
    @Mapping(target = "memberId", ignore = true) // Service asigna desde token
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "reservedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "attended", constant = "false")
    ClassReservation toEntity(ClassReservationRequest request);

    @Mapping(target = "reservationId", source = "entity.id")
    @Mapping(target = "classId", source = "entity.classEntity.id")
    @Mapping(target = "className", source = "entity.classEntity.className")
    @Mapping(target = "trainerName", expression = "java(entity.getClassEntity().getTrainer().getFirstName() + \" \" + entity.getClassEntity().getTrainer().getLastName())")
    @Mapping(target = "locationName", source = "entity.classEntity.location.name")
    @Mapping(target = "schedule", expression = "java(entity.getClassEntity().getStartTime() + \" - \" + entity.getClassEntity().getEndTime())")
    @Mapping(target = "capacity", expression = "java(entity.getClassEntity().getReservations().stream().filter(r -> r.getStatus() == com.classes.enums.ReservationStatus.RESERVADO).count() + \"/\" + entity.getClassEntity().getMaxCapacity() + \" inscritos\")")
    @Mapping(target = "action", expression = "java(entity.getStatus().name())")
    @Mapping(target = "alreadyReserved", expression = "java(entity.getStatus() != com.classes.enums.ReservationStatus.CANCELADO)")
    @Mapping(target = "completed", expression = "java(entity.getStatus() == com.classes.enums.ReservationStatus.COMPLETADO)")
    ClassReservationResponse toResponse(ClassReservation entity);

    List<ClassReservationResponse> toResponseList(List<ClassReservation> entities);
}
