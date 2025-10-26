package com.classes.services.Impl;

import com.classes.enums.AttendanceStatus;
import com.classes.enums.ClassStatus;
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
import java.time.LocalDateTime;
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
    private final ClassReservationRepository reservationRepository;
    private final MemberClientService memberClientService;


    @Transactional
    @Override
    public ClassResponse createClass(ClassRequest request) {

        ClassEntity entity = classMapper.toEntity(request);
        TrainerEntity trainer = trainerRepository.findById(request.getTrainerId())
                .orElseThrow(() -> new IllegalArgumentException("El trainer con ID " + request.getTrainerId() + " no existe"));
        LocationEntity location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new IllegalArgumentException("La ubicación con ID " + request.getLocationId() + " no existe"));


        if (request.getMaxCapacity() > location.getAbility()) {
            throw new IllegalArgumentException(
                    String.format("La capacidad de la clase (%d) no puede exceder la capacidad del local '%s' (%d)",
                            request.getMaxCapacity(), location.getName(), location.getAbility())
            );
        }

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

        if (request.getMaxCapacity() > 0 && request.getMaxCapacity() > existing.getLocation().getAbility()) {
            throw new IllegalArgumentException(
                    String.format("La capacidad de la clase (%d) no puede exceder la capacidad del local '%s' (%d)",
                            request.getMaxCapacity(), existing.getLocation().getName(), existing.getLocation().getAbility())
            );
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

    @Override
    @Transactional(readOnly = true)
    public MonthlyCalendarDTO getMonthlyCalendar(int year, int month) {
        log.info("Obteniendo calendario mensual para {}/{}", month, year);
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

    @Override
    @Transactional
    public ClassResponse startClass(UUID classId) {
        log.info("Iniciando clase con ID: {}", classId);

        ClassEntity classEntity = repository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Clase no encontrada"));

        if (classEntity.getStatus() != ClassStatus.PROGRAMADA) {
            throw new RuntimeException("La clase debe estar en estado PROGRAMADA para poder iniciarla");
        }

        classEntity.setStatus(ClassStatus.EN_PROCESO);
        ClassEntity saved = repository.save(classEntity);

        log.info("Clase {} cambiada a estado EN_PROCESO", classId);
        return classMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ClassResponse completeClass(UUID classId) {
        log.info("Completando clase con ID: {}", classId);

        ClassEntity classEntity = repository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Clase no encontrada"));

        if (classEntity.getStatus() != ClassStatus.EN_PROCESO) {
            throw new RuntimeException("La clase debe estar EN_PROCESO para poder completarla");
        }

        classEntity.setStatus(ClassStatus.COMPLETADA);
        ClassEntity saved = repository.save(classEntity);


        List<ClassReservation> reservations = reservationRepository.findByClassEntityId(classId);
        log.info("🔍 DEBUG - Procesando {} reservas para la clase {}", reservations.size(), classId);
        
        for (ClassReservation reservation : reservations) {
            log.info("🔍 DEBUG - Reserva {}: status={}, attendanceStatus={}, memberId={}", 
                reservation.getId(), 
                reservation.getStatus(),
                reservation.getAttendanceStatus(),
                reservation.getMemberId());

            if (reservation.getAttendanceStatus() == AttendanceStatus.PRESENTE ||
                reservation.getAttendanceStatus() == AttendanceStatus.TARDE ||
                reservation.getAttendanceStatus() == AttendanceStatus.JUSTIFICADO) {
                
                reservation.setStatus(ReservationStatus.COMPLETADO);
                log.info("Reserva {} marcada como COMPLETADO (asistencia: {})",
                    reservation.getId(), reservation.getAttendanceStatus());
            } 

            else {
                reservation.setStatus(ReservationStatus.PENDIENTE);
                log.info("Reserva {} marcada como PENDIENTE (asistencia: {})",
                    reservation.getId(), 
                    reservation.getAttendanceStatus() != null ? reservation.getAttendanceStatus() : "SIN_MARCAR");
            }
        }
        
        reservationRepository.saveAll(reservations);
        log.info("Actualizadas {} reservas según asistencia", reservations.size());

        log.info("Clase {} cambiada a estado COMPLETADA", classId);
        return classMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ClassResponse cancelClass(UUID classId) {
        log.info("Cancelando clase con ID: {}", classId);

        ClassEntity classEntity = repository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Clase no encontrada"));

        if (classEntity.getStatus() == ClassStatus.COMPLETADA) {
            throw new RuntimeException("No se puede cancelar una clase que ya fue completada");
        }

        classEntity.setStatus(ClassStatus.CANCELADA);
        classEntity.setActive(false);
        ClassEntity saved = repository.save(classEntity);

        log.info("Clase {} cambiada a estado CANCELADA", classId);
        return classMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ClassDetailResponse getClassDetails(UUID classId) {
        log.info("Obteniendo detalles completos de la clase: {}", classId);

        // 1. Obtener la clase
        ClassEntity classEntity = repository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Clase no encontrada con ID: " + classId));

        // 2. Obtener todas las reservas de esta clase
        List<ClassReservation> reservations = reservationRepository.findByClassEntityId(classId);
        log.info("Encontradas {} reservas para la clase", reservations.size());

        // 3. Obtener IDs de los miembros
        List<UUID> memberIds = reservations.stream()
                .map(ClassReservation::getMemberId)
                .collect(Collectors.toList());


        List<MemberInfoDTO> membersInfo = memberClientService.getMembersInfo(memberIds);
        log.info("Obtenida información de {} miembros desde msvc-members", membersInfo.size());

        Map<UUID, MemberInfoDTO> memberInfoMap = membersInfo.stream()
                .collect(Collectors.toMap(MemberInfoDTO::getUserId, m -> m));


        List<StudentInClassDTO> students = reservations.stream()
                .map(reservation -> {
                    MemberInfoDTO memberInfo = memberInfoMap.get(reservation.getMemberId());

                    if (memberInfo == null) {
                        log.warn("No se encontró información del miembro: {}", reservation.getMemberId());
                        return null;
                    }


                    String attendanceStatusStr = null;
                    if (reservation.getAttendanceStatus() != null) {
                        attendanceStatusStr = reservation.getAttendanceStatus().name();
                    }

                    return StudentInClassDTO.builder()
                            .memberId(memberInfo.getUserId())
                            .reservationId(reservation.getId())
                            .name(memberInfo.getFirstName() + " " + memberInfo.getLastName())
                            .email(memberInfo.getEmail())
                            .avatarInitials(memberInfo.getInitials())
                            .profileImageUrl(memberInfo.getProfileImageUrl())
                            .status(memberInfo.getStatus())
                            .membershipType(memberInfo.getMembershipType() != null ?
                                    memberInfo.getMembershipType().getType() : "Sin membresía")
                            .attendanceStatus(attendanceStatusStr) // null = sin marcar aún
                            .checkInTime(reservation.getCheckInTime())
                            .attendancePercentage(0.0) // TODO: Calcular desde historial
                            .totalClasses(0) // TODO: Calcular desde historial
                            .lastAccess(memberInfo.getLastAccess())
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        log.info("Lista de estudiantes construida: {} estudiantes", students.size());


        String schedule = classEntity.getStartTime() + " - " + classEntity.getEndTime();

        return ClassDetailResponse.builder()
                .id(classEntity.getId())
                .className(classEntity.getClassName())
                .description(classEntity.getDescription())
                .currentStudents(students.size())
                .maxCapacity(classEntity.getMaxCapacity())
                .trainerName(classEntity.getTrainer().getFirstName() + " " +
                        classEntity.getTrainer().getLastName())
                .locationName(classEntity.getLocation().getName())
                .classDate(classEntity.getClassDate())
                .startTime(classEntity.getStartTime())
                .endTime(classEntity.getEndTime())
                .schedule(schedule)
                .active(classEntity.isActive())
                .status(classEntity.getStatus().name())
                .students(students)
                .build();
    }

    @Override
    @Transactional
    public void updateAttendanceStatus(UUID reservationId, String attendanceStatus) {
        log.info("Actualizando estado de asistencia para reserva: {} -> {}", reservationId, attendanceStatus);

        ClassReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + reservationId));

        try {
            AttendanceStatus status = AttendanceStatus.valueOf(attendanceStatus.toUpperCase());
            reservation.setAttendanceStatus(status);

            // Si marca como PRESENTE o TARDE, guardar la hora actual
            if (status == AttendanceStatus.PRESENTE || status == AttendanceStatus.TARDE) {
                reservation.setCheckInTime(LocalDateTime.now());
                reservation.setAttended(true);
            } else {
                reservation.setCheckInTime(null);
                reservation.setAttended(false);
            }

            reservationRepository.save(reservation);
            log.info("Estado de asistencia actualizado correctamente");

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Estado de asistencia inválido: " + attendanceStatus +
                    ". Valores permitidos: PRESENTE, AUSENTE, TARDE, JUSTIFICADO");
        }
    }
}
