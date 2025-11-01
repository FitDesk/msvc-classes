package com.classes.repositories;


import com.classes.entities.LocationEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface  LocationRepository extends JpaRepository<LocationEntity, UUID> {
    Page<LocationEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<LocationEntity> findByActiveTrue(Pageable pageable);

    Page<LocationEntity> findByActiveFalse(Pageable pageable);
    Page<LocationEntity> findByNameContainingIgnoreCaseAndActive(String name, boolean active, Pageable pageable);

}
