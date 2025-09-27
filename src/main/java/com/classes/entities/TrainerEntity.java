package com.classes.entities;

import com.classes.config.Audit;
import com.classes.config.AuditListener;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "trainers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditListener.class)
public class TrainerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "user_id")
    private UUID userId;
    private String bio;
    @Column(columnDefinition = "jsonb")
    private String availableHours;
    @Embedded
    private Audit audit;
}