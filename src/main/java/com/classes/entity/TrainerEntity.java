package com.classes.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "trainers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name="user_id")
    private UUID userId;
    private String bio;
    @Column(columnDefinition = "jsonb")
    private String availableHours;
}