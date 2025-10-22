package com.classes.services.Impl;

import com.classes.dtos.Dashboard.MemberDashboardDTO;
import com.classes.dtos.Dashboard.UpcomingClassDTO;
import com.classes.dtos.Dashboard.WeeklyActivityDTO;
import com.classes.entities.ClassReservation;
import com.classes.enums.ReservationStatus;
import com.classes.repositories.ClassReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class DashboardServiceImpl {
    private final ClassReservationRepository reservationRepository;

    public MemberDashboardDTO getDashboardForMember(UUID memberId) {

        List<ClassReservation> reservations = reservationRepository.findByMemberId(memberId);
        List<ClassReservation> activeReservations = reservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.RESERVADO || Boolean.TRUE.equals(r.getAttended()))
                .toList();
        LocalDateTime now = LocalDateTime.now();
        boolean inClass = activeReservations.stream()
                .anyMatch(r -> {
                    LocalDateTime start = LocalDateTime.of(LocalDate.now(), r.getClassEntity().getStartTime());
                    LocalDateTime end = LocalDateTime.of(LocalDate.now(), r.getClassEntity().getEndTime());
                    return !now.isBefore(start) && !now.isAfter(end)
                            && r.getStatus() == ReservationStatus.RESERVADO;
                });
        Optional<ClassReservation> nextClassOpt = activeReservations.stream()
                .filter(r -> {
                    LocalDateTime start = LocalDateTime.of(LocalDate.now(), r.getClassEntity().getStartTime());
                    return start.isAfter(now);
                })
                .sorted(Comparator.comparing(r -> r.getClassEntity().getStartTime()))
                .findFirst();
        int totalReserved = (int) activeReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.RESERVADO).count();
        int attended = (int) activeReservations.stream()
                .filter(r -> Boolean.TRUE.equals(r.getAttended()))
                .count();
        int remaining = Math.max(totalReserved - attended, 0);
        int consecutiveDays = calcularDiasConsecutivos(activeReservations);
        List<WeeklyActivityDTO> weeklyActivity = calcularActividadSemanal(activeReservations);
        List<UpcomingClassDTO> upcoming = activeReservations.stream()
                .filter(r -> {
                    LocalDateTime start = LocalDateTime.of(LocalDate.now(), r.getClassEntity().getStartTime());
                    return start.isAfter(now);
                })
                .sorted(Comparator.comparing(r -> r.getClassEntity().getStartTime()))
                .skip(1)
                .limit(3)
                .map(r -> UpcomingClassDTO.builder()
                        .className(r.getClassEntity().getClassName())
                        .trainerName(r.getClassEntity().getTrainer().getFirstName() + " " +
                                r.getClassEntity().getTrainer().getLastName())
                        .schedule(r.getClassEntity().getStartTime() + " - " + r.getClassEntity().getEndTime())
                        .location(r.getClassEntity().getLocation().getName())
                        .build())
                .toList();
        return MemberDashboardDTO.builder()
                .inClass(inClass)
                .remainingClasses(remaining)
                .nextClassName(nextClassOpt.map(r -> r.getClassEntity().getClassName()).orElse(null))
                .nextClassTime(nextClassOpt.map(r -> r.getClassEntity().getStartTime().toString()).orElse(null))
                .consecutiveDays(consecutiveDays)
                .weeklyActivity(weeklyActivity)
                .upcomingClasses(upcoming)
                .build();
    }


    private int calcularDiasConsecutivos(List<ClassReservation> reservations) {
        var dates = reservations.stream()
                .filter(r -> Boolean.TRUE.equals(r.getAttended()))
                .map(r -> r.getReservedAt().toLocalDate())
                .distinct()
                .sorted(Comparator.reverseOrder())
                .toList();

        int streak = 0;
        LocalDate prev = LocalDate.now();
        for (LocalDate date : dates) {
            if (date.equals(prev) || date.equals(prev.minusDays(1))) {
                streak++;
                prev = date;
            } else break;
        }
        return streak;
    }

    private List<WeeklyActivityDTO> calcularActividadSemanal(List<ClassReservation> reservations) {
        Map<DayOfWeek, Long> map = reservations.stream()
                .filter(r -> Boolean.TRUE.equals(r.getAttended()))
                .collect(Collectors.groupingBy(r -> r.getReservedAt().getDayOfWeek(), Collectors.counting()));

        return Arrays.stream(DayOfWeek.values())
                .map(day -> WeeklyActivityDTO.builder()
                        .day(day.name())
                        .sessions(map.getOrDefault(day, 0L).intValue())
                        .build())
                .toList();
    }
}
