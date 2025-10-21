package com.classes.repositories;

import com.classes.entities.TrainerEntity;
import com.classes.enums.TrainerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface TrainerRepository extends JpaRepository<TrainerEntity, UUID> {
    Optional<TrainerEntity> findByEmail(String email);
    Optional<TrainerEntity> findByDni(String dni);
    boolean existsByEmail(String email);
    Page<TrainerEntity> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName, String lastName, Pageable pageable);
    Page<TrainerEntity> findByStatus(TrainerStatus status, Pageable pageable);
}
