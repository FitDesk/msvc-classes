package com.classes.services.Impl;

import com.classes.dtos.Dashboard.StudentTrendDTO;
import com.classes.dtos.Dashboard.TrainerDashboardDTO;
import com.classes.entities.ClassEntity;
import com.classes.entities.ClassReservation;
import com.classes.repositories.ClassRepository;
import com.classes.repositories.ClassReservationRepository;
import com.classes.services.TrainerDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainerDashboardServiceImpl implements TrainerDashboardService {

    private final ClassRepository classRepository;
    private final ClassReservationRepository reservationRepository;

    @Override
    @Transactional(readOnly = true)
    public TrainerDashboardDTO getDashboardForTrainer(UUID trainerId) {

        List<ClassEntity> trainerClasses = classRepository.findByTrainerId(trainerId);
        int totalClasses = trainerClasses.size();

        long completedClasses = classRepository.countCompletedClassesByTrainerId(trainerId);

        long upcomingClasses = classRepository.countUpcomingClassesByTrainerId(trainerId, LocalDate.now());

        Set<UUID> uniqueStudents = new HashSet<>();
        for (ClassEntity classEntity : trainerClasses) {
            List<ClassReservation> reservations = reservationRepository.findByClassEntityId(classEntity.getId());
            uniqueStudents.addAll(reservations.stream()
                    .map(ClassReservation::getMemberId)
                    .collect(Collectors.toSet()));
        }
        int totalStudents = uniqueStudents.size();
        Double avgAttendance = reservationRepository.calculateAverageAttendanceByTrainerId(trainerId);
        double averageAttendance = avgAttendance != null ? avgAttendance : 0.0;
        LocalDate now = LocalDate.now();
        long classesThisMonth = classRepository.countByTrainerIdAndMonth(
                trainerId, now.getYear(), now.getMonthValue());
        LocalDate lastMonth = now.minusMonths(1);
        long classesLastMonth = classRepository.countByTrainerIdAndMonth(
                trainerId, lastMonth.getYear(), lastMonth.getMonthValue());
        double attendanceChange = 0.0;
        if (classesLastMonth > 0) {
            attendanceChange = ((double) classesThisMonth - classesLastMonth) / classesLastMonth * 100;
        } else if (classesThisMonth > 0) {
            attendanceChange = 100.0;
        }

        List<StudentTrendDTO> studentTrends = calculateStudentTrends(trainerClasses);

        return TrainerDashboardDTO.builder()
                .totalClasses(totalClasses)
                .completedClasses((int) completedClasses)
                .totalStudents(totalStudents)
                .averageAttendance(averageAttendance)
                .upcomingClasses((int) upcomingClasses)
                .classesThisMonth((int) classesThisMonth)
                .attendanceChange(attendanceChange)
                .studentTrends(studentTrends)
                .build();
    }

    private List<StudentTrendDTO> calculateStudentTrends(List<ClassEntity> classes) {
        Map<Integer, Set<UUID>> activeByWeek = new HashMap<>();
        Map<Integer, Set<UUID>> inactiveByWeek = new HashMap<>();
        LocalDate now = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        for (int i = 3; i >= 0; i--) {
            LocalDate weekDate = now.minusWeeks(i);
            int weekNumber = weekDate.get(weekFields.weekOfWeekBasedYear());
            activeByWeek.put(weekNumber, new HashSet<>());
            inactiveByWeek.put(weekNumber, new HashSet<>());
        }

        for (ClassEntity classEntity : classes) {
            LocalDate classDate = classEntity.getClassDate();
            if (classDate.isAfter(now.minusWeeks(4)) && !classDate.isAfter(now)) {
                int weekNumber = classDate.get(weekFields.weekOfWeekBasedYear());
                if (activeByWeek.containsKey(weekNumber)) {
                    List<ClassReservation> reservations = reservationRepository
                            .findByClassEntityId(classEntity.getId());
                    for (ClassReservation reservation : reservations) {
                        if (Boolean.TRUE.equals(reservation.getAttended())) {
                            activeByWeek.get(weekNumber).add(reservation.getMemberId());
                        } else {
                            inactiveByWeek.get(weekNumber).add(reservation.getMemberId());
                        }
                    }
                }
            }
        }
        List<StudentTrendDTO> trends = new ArrayList<>();
        List<Integer> sortedWeeks = new ArrayList<>(activeByWeek.keySet());
        Collections.sort(sortedWeeks);

        int weekIndex = 1;
        for (Integer weekNumber : sortedWeeks) {
            int active = activeByWeek.get(weekNumber).size();
            int inactive = inactiveByWeek.get(weekNumber).size();

            trends.add(StudentTrendDTO.builder()
                    .week("Sem " + weekIndex)
                    .activeStudents(active)
                    .inactiveStudents(inactive)
                    .label("Semana " + weekIndex + ": " + active + " activos, " + inactive + " inactivos")
                    .build());

            weekIndex++;
        }

        return trends;
    }
}
