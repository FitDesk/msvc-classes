package com.classes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "classes", indexes = {@Index(columnList="start_time"), @Index(columnList="trainer_id")})
@AllArgsConstructor
@NoArgsConstructor
public class ClassEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String title;
    @Column(length = 2000)
    private String description;
    @Column(name = "trainer_id")
    private UUID trainerId;
    @Column(name = "max_capacity")
    private Integer maxCapacity;
//    @Enumerated(EnumType.STRING)
//    private ClassStatus status; // SCHEDULED,CANCELLED,COMPLETED
}
