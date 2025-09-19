package com.classes.entities;

import com.classes.config.Audit;
import com.classes.config.AuditListener;
import jakarta.persistence.*;
import lombok.*;

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
    private String title;
    @Column(length = 2000)
    private String description;
    @Column(name = "trainer_id")
    private UUID trainerId;
    @Column(name = "max_capacity")
    private Integer maxCapacity;
    @Embedded
    private Audit audit;
}
