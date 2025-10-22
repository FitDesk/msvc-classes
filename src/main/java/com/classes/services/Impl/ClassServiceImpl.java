package com.classes.services.Impl;

import com.classes.services.ClassStatsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.classes.dtos.Class.*;
import com.classes.dtos.external.MemberInfoDTO;
import com.classes.entities.ClassEntity;
import com.classes.entities.ClassReservation;
import com.classes.entities.LocationEntity;
import com.classes.entities.TrainerEntity;
import com.classes.enums.ReservationStatus;
import com.classes.mappers.ClassMapper;
import com.classes.mappers.ClassStatsMapper;
import com.classes.repositories.ClassRepository;
import com.classes.repositories.ClassReservationRepository;
import com.classes.repositories.LocationRepository;
import com.classes.repositories.TrainerRepository;
import com.classes.services.ClassService;
import com.classes.services.MemberClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class ClassServiceImpl implements ClassService {

    private final ClassRepository repository;
    private final ClassMapper classMapper;
    private final TrainerRepository trainerRepository;
    private final LocationRepository locationRepository;
    private final ClassStatsService classStatsService;


    @Transactional
    @Override
    public ClassResponse createClass(ClassRequest request) {
        validateTrainerAndLocation(request);
        ClassEntity entity = classMapper.toEntity(request);
        TrainerEntity trainer = trainerRepository.findById(request.getTrainerId())
                .orElseThrow(() -> new IllegalArgumentException("El trainer con ID " + request.getTrainerId() + " no existe"));
        LocationEntity location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new IllegalArgumentException("La ubicación con ID " + request.getLocationId() + " no existe"));
        entity.setTrainer(trainer);
        entity.setLocation(location);
        ClassEntity saved = repository.save(entity);
        return classMapper.toResponse(saved);
    }

    @Transactional
    @Override
    public List<ClassResponse> findAll() {
        return classMapper.toResponseList(repository.findAll());
    }

    @Transactional
    @Override
    public Page<ClassResponse> findAllPaginated(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);

        if (search != null && !search.trim().isEmpty()) {
            return repository.findByClassNameContainingIgnoreCaseOrTrainerFirstNameContainingIgnoreCaseOrTrainerLastNameContainingIgnoreCase(
                search, search, search, pageable
            ).map(classMapper::toResponse);
        } else {
            return repository.findAll(pageable).map(classMapper::toResponse);
        }
    }

    @Transactional
    @Override
    public ClassResponse updateClass(UUID id, ClassRequest request) {

        ClassEntity existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La clase con ID " + id + " no existe"));


        if (request.getTrainerId() != null) {
            TrainerEntity trainer = trainerRepository.findById(request.getTrainerId())
                    .orElseThrow(() -> new IllegalArgumentException("El trainer con ID " + request.getTrainerId() + " no existe"));
            existing.setTrainer(trainer);
        }


        if (request.getLocationId() != null) {
            LocationEntity location = locationRepository.findById(request.getLocationId())
                    .orElseThrow(() -> new IllegalArgumentException("La ubicación con ID " + request.getLocationId() + " no existe"));
            existing.setLocation(location);
        }
        classMapper.updateFromRequest(request, existing);
        ClassEntity updated = repository.save(existing);
        return classMapper.toResponse(updated);
    }

    @Transactional
    @Override
    public void deleteClass(UUID id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("La clase con ID " + id + " no existe");
        }
        repository.deleteById(id);
    }

    private void validateTrainerAndLocation(ClassRequest request) {
        if (!trainerRepository.existsById(request.getTrainerId())) {
            throw new IllegalArgumentException("El trainer con ID " + request.getTrainerId() + " no existe");
        }
        if (!locationRepository.existsById(request.getLocationId())) {
            throw new IllegalArgumentException("La ubicación con ID " + request.getLocationId() + " no existe");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public MonthlyCalendarDTO getMonthlyCalendar(int year, int month) {
        log.info("📅 Obteniendo calendario mensual para {}/{}", month, year);
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();
        List<CalendarClassDTO> allClasses = classStatsService.getClassesForCalendar(firstDay, lastDay);
        Map<LocalDate, List<CalendarClassDTO>> classesByDate = allClasses.stream()
                .collect(Collectors.groupingBy(CalendarClassDTO::getClassDate));
        List<LocalDate> daysWithClasses = new ArrayList<>(classesByDate.keySet());
        Collections.sort(daysWithClasses);
        String monthName = yearMonth.getMonth().getDisplayName(TextStyle.FULL, new java.util.Locale("es", "ES"));
        monthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1);
        return MonthlyCalendarDTO.builder()
                .year(year)
                .month(month)
                .monthName(monthName)
                .firstDayOfMonth(firstDay)
                .lastDayOfMonth(lastDay)
                .classesByDate(classesByDate)
                .totalClasses(allClasses.size())
                .daysWithClasses(daysWithClasses)
                .build();
    }
}
