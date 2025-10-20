package com.classes.repositories;

import com.classes.entities.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, UUID> {
    Optional<ClassEntity> findFirstByTrainerId(UUID trainerId);
    Optional<ClassEntity> findFirstByLocationId(UUID locationId);

    List<ClassEntity> findByTrainerId(UUID trainerId);
    

    List<ClassEntity> findByActiveTrue();
    

    List<ClassEntity> findByClassDateBetween(LocalDate startDate, LocalDate endDate);
    

    @Query("SELECT c FROM ClassEntity c WHERE c.classDate >= :currentDate ORDER BY c.classDate, c.startTime")
    List<ClassEntity> findUpcomingClasses(@Param("currentDate") LocalDate currentDate);
    

    @Query("SELECT COUNT(c) FROM ClassEntity c WHERE c.trainer.id = :trainerId AND YEAR(c.classDate) = :year AND MONTH(c.classDate) = :month")
    long countByTrainerIdAndMonth(@Param("trainerId") UUID trainerId, @Param("year") int year, @Param("month") int month);
}
