package com.classes.repositories;

import com.classes.entities.ClassReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClassReservationRepository extends JpaRepository<ClassReservation, UUID> {
    List<ClassReservation> findByMemberId(UUID memberId);
    List<ClassReservation> findByClassEntityId(UUID classId);
    Optional<ClassReservation> findByClassEntityIdAndMemberId(UUID classId, UUID memberId);
}
