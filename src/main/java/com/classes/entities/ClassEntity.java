package com.classes.entities;

import com.classes.config.Audit;
import com.classes.config.AuditListener;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "classes")
@EntityListeners(AuditListener.class)
public class ClassEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;

    private String className;
    private int duration;

    private int maxCapacity;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean active;
    private String description;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private LocationEntity location;

    @ManyToOne
    @JoinColumn(name = "trainer_id")
    private TrainerEntity trainer;

    @Embedded
    private Audit audit;
}
