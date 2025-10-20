package com.classes.services.Impl;

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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class ClassServiceImpl implements ClassService {

    private final ClassRepository repository;
    private final ClassMapper classMapper;
    private final ClassStatsMapper classStatsMapper;
    private final TrainerRepository trainerRepository;
    private final LocationRepository locationRepository;
    private final ClassReservationRepository reservationRepository;
    private final MemberClientService memberClientService;

    //<---CRUD---->
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

    //<---ESTADISTICAS---->

    @Override
    @Transactional(readOnly = true)
    public List<ClassWithStatsResponse> getClassesWithStatsByTrainer(UUID trainerId) {
        log.info("📊 Obteniendo clases con estadísticas para el trainer {}", trainerId);
        List<ClassEntity> classes = repository.findByTrainerId(trainerId);
        return classes.stream().map(classEntity -> {
            ClassWithStatsResponse response = classStatsMapper.toClassWithStatsResponse(classEntity);
            long currentStudents = reservationRepository.countByClassEntityIdAndStatus(
                    classEntity.getId(), ReservationStatus.RESERVADO);
            response.setCurrentStudents((int) currentStudents);
            Double avgAttendance = reservationRepository.calculateAverageAttendanceByClassId(classEntity.getId());
            response.setAverageAttendance(avgAttendance != null ? avgAttendance : 0.0);
            response.setStatus(determineClassStatus(classEntity, (int) currentStudents));
            
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClassDetailResponse getClassDetail(UUID classId) {
        log.info("🔍 Obteniendo detalle de la clase {}", classId);
        
        ClassEntity classEntity = repository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("La clase con ID " + classId + " no existe"));
        ClassDetailResponse response = classStatsMapper.toClassDetailResponse(classEntity);
        List<ClassReservation> reservations = reservationRepository.findByClassEntityId(classId);
        List<UUID> memberIds = reservations.stream()
                .map(ClassReservation::getMemberId)
                .distinct()
                .collect(Collectors.toList());
        List<MemberInfoDTO> membersInfo = memberClientService.getMembersInfo(memberIds);
        List<StudentInClassDTO> students = reservations.stream()
                .map(reservation -> buildStudentDTO(reservation, membersInfo))
                .collect(Collectors.toList());
        
        response.setCurrentStudents(students.size());
        response.setStudents(students);
        
        return response;
    }
    
    private StudentInClassDTO buildStudentDTO(ClassReservation reservation, List<MemberInfoDTO> membersInfo) {
        MemberInfoDTO memberInfo = membersInfo.stream()
                .filter(m -> m.getId().equals(reservation.getMemberId()))
                .findFirst()
                .orElse(createDefaultMemberInfo(reservation.getMemberId()));

        List<ClassReservation> memberReservations = 
                reservationRepository.findByMemberId(reservation.getMemberId());

        long totalClasses = memberReservations.size();

        // ✅ Cambiado: uso de Boolean.TRUE.equals() para evitar errores por null
        long attendedClasses = memberReservations.stream()
                .filter(r -> Boolean.TRUE.equals(r.getAttended()))
                .count();

        double attendancePercentage = totalClasses > 0 
                ? (attendedClasses * 100.0 / totalClasses) : 0.0;
        
        String initials = getInitials(memberInfo.getFirstName(), memberInfo.getLastName());
        
        return StudentInClassDTO.builder()
                .memberId(reservation.getMemberId())
                .name(memberInfo.getFirstName() + " " + memberInfo.getLastName())
                .email(memberInfo.getEmail())
                .avatarInitials(initials)
                .status(memberInfo.getStatus())
                .membershipType(memberInfo.getMembershipType())
                .attendancePercentage(attendancePercentage)
                .totalClasses((int) totalClasses)
                .lastAccess(memberInfo.getLastAccess())
                .reservationId(reservation.getId())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CalendarClassDTO> getClassesForCalendar(LocalDate startDate, LocalDate endDate) {
        log.info("📅 Obteniendo clases para calendario entre {} y {}", startDate, endDate);
        
        List<ClassEntity> classes = repository.findByClassDateBetween(startDate, endDate);
        return mapToCalendarDTOs(classes);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CalendarClassDTO> getUpcomingClasses() {
        log.info("📅 Obteniendo próximas clases");
        
        List<ClassEntity> classes = repository.findUpcomingClasses(LocalDate.now());
        return mapToCalendarDTOs(classes);
    }
    private List<CalendarClassDTO> mapToCalendarDTOs(List<ClassEntity> classes) {
        return classes.stream().map(classEntity -> {
            CalendarClassDTO dto = classStatsMapper.toCalendarDTO(classEntity);
            long currentStudents = reservationRepository.countByClassEntityIdAndStatus(
                    classEntity.getId(), ReservationStatus.RESERVADO);
            dto.setCurrentStudents((int) currentStudents);
            String action = determineAction(classEntity, (int) currentStudents);
            dto.setAction(action);
            return dto;
        }).collect(Collectors.toList());
    }

    private String determineClassStatus(ClassEntity classEntity, int currentStudents) {
        if (!classEntity.isActive()) {
            return "Cancelada";
        }
        if (currentStudents >= classEntity.getMaxCapacity()) {
            return "Llena";
        }
        return "Activa";
    }

    private String determineAction(ClassEntity classEntity, int currentStudents) {
        if (!classEntity.isActive()) {
            return "Cancelada";
        }
        if (currentStudents >= classEntity.getMaxCapacity()) {
            return "Llena";
        }
        if (currentStudents >= classEntity.getMaxCapacity() * 0.9) {
            return "Lista de espera";
        }
        return "Reservar";
    }


    private MemberInfoDTO createDefaultMemberInfo(UUID memberId) {
        return MemberInfoDTO.builder()
                .id(memberId)
                .firstName("Usuario")
                .lastName("Desconocido")
                .email("no-disponible@email.com")
                .status("DESCONOCIDO")
                .membershipType("N/A")
                .build();
    }

    private String getInitials(String firstName, String lastName) {
        String first = firstName != null && !firstName.isEmpty() ? firstName.substring(0, 1) : "";
        String last = lastName != null && !lastName.isEmpty() ? lastName.substring(0, 1) : "";
        return (first + last).toUpperCase();
    }
}
