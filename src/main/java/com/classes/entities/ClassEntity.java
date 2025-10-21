package com.classes.entities;

import com.classes.config.audit.Audit;
import com.classes.config.audit.AuditListener;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
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
    private String className;
    private int duration;

    private int maxCapacity;
    private LocalDate classDate;
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

    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ClassReservation> reservations = new ArrayList<>();


    @Embedded
    private Audit audit;
}
