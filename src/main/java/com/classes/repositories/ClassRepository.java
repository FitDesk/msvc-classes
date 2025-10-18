package com.classes.repositories;

import com.classes.entities.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, UUID> {
    Optional<ClassEntity> findFirstByTrainerId(UUID trainerId);
    Optional<ClassEntity> findFirstByLocationId(UUID locationId);
    
    Page<ClassEntity> findByClassNameContainingIgnoreCaseOrTrainerFirstNameContainingIgnoreCaseOrTrainerLastNameContainingIgnoreCase(
        String className, String trainerFirstName, String trainerLastName, Pageable pageable);

}
