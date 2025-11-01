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
    List<ClassReservation> findByMemberId(UUID memberId);

    List<ClassReservation> findByClassEntityId(UUID classId);

    Optional<ClassReservation> findByClassEntityIdAndMemberId(UUID classId, UUID memberId);

    @Query("SELECT COUNT(r) FROM ClassReservation r WHERE r.classEntity.id = :classId AND r.status = :status")
    long countByClassEntityIdAndStatus(@Param("classId") UUID classId,
                                       @Param("status") ReservationStatus status);

    List<ClassReservation> findByMemberIdAndStatus(UUID memberId, ReservationStatus status);

    @Query("""
           SELECT COALESCE(AVG(CASE WHEN r.attended = true THEN 1.0 ELSE 0.0 END) * 100, 0)
           FROM ClassReservation r
           WHERE r.classEntity.id = :classId
           """)
    Double calculateAverageAttendanceByClassId(@Param("classId") UUID classId);

    @Query("""
           SELECT r FROM ClassReservation r
           WHERE r.memberId = :memberId
           AND r.reservedAt BETWEEN :startDate AND :endDate
           """)
    List<ClassReservation> findByMemberIdAndDateRange(@Param("memberId") UUID memberId,
                                                      @Param("startDate") LocalDateTime startDate,
                                                      @Param("endDate") LocalDateTime endDate);

    @Query("""
           SELECT COUNT(r) FROM ClassReservation r
           WHERE r.classEntity.trainer.id = :trainerId
           """)
    long countReservationsByTrainerId(@Param("trainerId") UUID trainerId);

    @Query("""
           SELECT COALESCE(AVG(CASE WHEN r.attended = true THEN 1.0 ELSE 0.0 END) * 100, 0)
           FROM ClassReservation r
           WHERE r.classEntity.trainer.id = :trainerId
           """)
    Double calculateAverageAttendanceByTrainerId(@Param("trainerId") UUID trainerId);

    List<ClassReservation> findByAttendedTrue();

    List<ClassReservation> findByAttendedFalse();
}
