package com.classes.mappers;

import com.classes.config.MapStructConfig;
import com.classes.dtos.Class.CalendarClassDTO;
import com.classes.dtos.Class.ClassDetailResponse;
import com.classes.dtos.Class.ClassWithStatsResponse;
import com.classes.entities.ClassEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;


@Mapper(config = MapStructConfig.class)
public interface ClassStatsMapper {


    @Mapping(target = "trainerName", expression = "java(entity.getTrainer().getFirstName() + \" \" + entity.getTrainer().getLastName())")
    @Mapping(target = "locationName", source = "location.name")
    @Mapping(target = "schedule", expression = "java(formatScheduleWithDay(entity))")
    @Mapping(target = "currentStudents", ignore = true) // se calcula en servicio
    @Mapping(target = "averageAttendance", ignore = true) // se calcula en servicio
    @Mapping(target = "status", ignore = true) // se calcula en servicio
    ClassWithStatsResponse toClassWithStatsResponse(ClassEntity entity);


    @Mapping(target = "trainerName", expression = "java(entity.getTrainer().getFirstName() + \" \" + entity.getTrainer().getLastName())")
    @Mapping(target = "locationName", source = "location.name")
    @Mapping(target = "schedule", expression = "java(formatScheduleWithDay(entity))")
    @Mapping(target = "currentStudents", ignore = true) // se calcula en servicio
    @Mapping(target = "students", ignore = true) // se calcula en servicio
    ClassDetailResponse toClassDetailResponse(ClassEntity entity);


    @Mapping(target = "trainerName", expression = "java(entity.getTrainer().getFirstName() + \" \" + entity.getTrainer().getLastName())")
    @Mapping(target = "locationName", source = "location.name")
    @Mapping(target = "schedule", expression = "java(entity.getStartTime() + \" - \" + entity.getEndTime())")
    @Mapping(target = "currentStudents", ignore = true) // se calcula en servicio
    @Mapping(target = "action", ignore = true) // se calcula en servicio
    CalendarClassDTO toCalendarDTO(ClassEntity entity);

    List<CalendarClassDTO> toCalendarDTOList(List<ClassEntity> entities);


    default String formatScheduleWithDay(ClassEntity entity) {
        DayOfWeek dayOfWeek = entity.getClassDate().getDayOfWeek();
        String dayName = dayOfWeek.getDisplayName(TextStyle.SHORT, new Locale("es"));
        return dayName + " " + entity.getStartTime() + " - " + entity.getEndTime();
    }
}
