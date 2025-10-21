package com.classes.repositories;

import com.classes.entities.ClassReservation;
import com.classes.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClassReservationRepository extends JpaRepository<ClassReservation, UUID> {
    // 🔹 Obtener todas las reservas de un miembro
    List<ClassReservation> findByMemberId(UUID memberId);

    // 🔹 Obtener todas las reservas de una clase
    List<ClassReservation> findByClassEntityId(UUID classId);

    // 🔹 Buscar una reserva específica por clase y miembro
    Optional<ClassReservation> findByClassEntityIdAndMemberId(UUID classId, UUID memberId);

    // 🔹 Contar reservas activas por clase según estado
    @Query("SELECT COUNT(r) FROM ClassReservation r WHERE r.classEntity.id = :classId AND r.status = :status")
    long countByClassEntityIdAndStatus(@Param("classId") UUID classId,
                                       @Param("status") ReservationStatus status);

    // 🔹 Obtener reservas de un miembro por estado
    List<ClassReservation> findByMemberIdAndStatus(UUID memberId, ReservationStatus status);

    // 🔹 Calcular asistencia promedio (%) de una clase
    @Query("""
           SELECT COALESCE(AVG(CASE WHEN r.attended = true THEN 1.0 ELSE 0.0 END) * 100, 0)
           FROM ClassReservation r
           WHERE r.classEntity.id = :classId
           """)
    Double calculateAverageAttendanceByClassId(@Param("classId") UUID classId);

    // 🔹 Obtener reservas de un miembro dentro de un rango de fechas
    @Query("""
           SELECT r FROM ClassReservation r
           WHERE r.memberId = :memberId
           AND r.reservedAt BETWEEN :startDate AND :endDate
           """)
    List<ClassReservation> findByMemberIdAndDateRange(@Param("memberId") UUID memberId,
                                                      @Param("startDate") LocalDateTime startDate,
                                                      @Param("endDate") LocalDateTime endDate);

    // 🔹 Contar total de reservas gestionadas por un entrenador
    @Query("""
           SELECT COUNT(r) FROM ClassReservation r
           WHERE r.classEntity.trainer.id = :trainerId
           """)
    long countReservationsByTrainerId(@Param("trainerId") UUID trainerId);

    // 🔹 Calcular asistencia promedio (%) de un entrenador
    @Query("""
           SELECT COALESCE(AVG(CASE WHEN r.attended = true THEN 1.0 ELSE 0.0 END) * 100, 0)
           FROM ClassReservation r
           WHERE r.classEntity.trainer.id = :trainerId
           """)
    Double calculateAverageAttendanceByTrainerId(@Param("trainerId") UUID trainerId);

    // 🔹 Obtener todas las reservas con asistencia (true)
    List<ClassReservation> findByAttendedTrue();

    // 🔹 Obtener todas las reservas sin asistencia (false)
    List<ClassReservation> findByAttendedFalse();
}
