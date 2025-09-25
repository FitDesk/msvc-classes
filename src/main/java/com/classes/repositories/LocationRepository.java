package com.classes.repositories;


import com.classes.entities.LocationEntity;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface  LocationRepository extends JpaRepository<LocationEntity, UUID> {
}
