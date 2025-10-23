package com.classes.services.Impl;

import com.classes.dtos.Class.CalendarClassDTO;
import com.classes.dtos.Class.ClassDetailResponse;
import com.classes.dtos.Class.ClassWithStatsResponse;
import com.classes.dtos.Class.StudentInClassDTO;
import com.classes.dtos.external.MemberInfoDTO;
import com.classes.entities.ClassEntity;
import com.classes.entities.ClassReservation;
import com.classes.enums.ReservationStatus;
import com.classes.mappers.ClassStatsMapper;
import com.classes.repositories.ClassRepository;
import com.classes.repositories.ClassReservationRepository;
import com.classes.services.ClassStatsService;
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
public class ClassStatsServiceImpl implements ClassStatsService {

    private final ClassRepository repository;
    private final ClassReservationRepository reservationRepository;
    private final ClassStatsMapper classStatsMapper;
    private final MemberClientService memberClientService;

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
        
        log.info("📋 Consultando información de {} miembros: {}", memberIds.size(), memberIds);
        List<MemberInfoDTO> membersInfo = memberClientService.getMembersInfo(memberIds);
        log.info("📊 Información obtenida de {} miembros", membersInfo.size());
        
        if (membersInfo.isEmpty() && !memberIds.isEmpty()) {
            log.warn("⚠️ No se pudo obtener información de ningún miembro. Usando datos por defecto.");
        }
        
        List<StudentInClassDTO> students = reservations.stream()
                .map(reservation -> buildStudentDTO(reservation, membersInfo))
                .collect(Collectors.toList());

        response.setCurrentStudents(students.size());
        response.setStudents(students);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CalendarClassDTO> getClassesForCalendar(LocalDate startDate, LocalDate endDate) {
        log.info("Obteniendo clases para calendario entre {} y {}", startDate, endDate);

        List<ClassEntity> classes = repository.findByClassDateBetween(startDate, endDate);
        return mapToCalendarDTOs(classes);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CalendarClassDTO> getUpcomingClasses() {
        log.info("Obteniendo próximas clases");

        List<ClassEntity> classes = repository.findUpcomingClasses(LocalDate.now());
        return mapToCalendarDTOs(classes);
    }

    // ==================== MÉTODOS PRIVADOS ====================

    private StudentInClassDTO buildStudentDTO(ClassReservation reservation, List<MemberInfoDTO> membersInfo) {
        MemberInfoDTO memberInfo = membersInfo.stream()
                .filter(m -> m.getUserId().equals(reservation.getMemberId()))
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
        
        // Extraer tipo de membresía
        String membershipType = "N/A";
        if (memberInfo.getMembershipType() != null) {
            membershipType = memberInfo.getMembershipType().getType();
        }

        return StudentInClassDTO.builder()
                .memberId(reservation.getMemberId())
                .name(memberInfo.getFirstName() + " " + memberInfo.getLastName())
                .email(memberInfo.getEmail())
                .avatarInitials(initials)
                .status(memberInfo.getStatus() != null ? memberInfo.getStatus() : "DESCONOCIDO")
                .membershipType(membershipType)
                .attendancePercentage(attendancePercentage)
                .totalClasses((int) totalClasses)
                .lastAccess(memberInfo.getLastAccess())
                .reservationId(reservation.getId())
                .build();
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
                .userId(memberId)
                .firstName("Usuario")
                .lastName("Desconocido")
                .email("no-disponible@email.com")
                .status("DESCONOCIDO")
                .membershipType(null)  // membership es null por defecto
                .build();
    }

    private String getInitials(String firstName, String lastName) {
        String first = firstName != null && !firstName.isEmpty() ? firstName.substring(0, 1) : "";
        String last = lastName != null && !lastName.isEmpty() ? lastName.substring(0, 1) : "";
        return (first + last).toUpperCase();
    }
}
