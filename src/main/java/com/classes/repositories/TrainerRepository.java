package com.classes.repositories;

import com.classes.entities.TrainerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface TrainerRepository extends JpaRepository<TrainerEntity, UUID> {
    Optional<TrainerEntity> findByEmail(String email);
    Optional<TrainerEntity> findByDni(String dni);
    boolean existsByEmail(String email);
}
