package com.classes.entities;

import com.classes.config.audit.Audit;
import com.classes.config.audit.AuditListener;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "location")
@EntityListeners(AuditListener.class)
public class LocationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String description;
    private int ability;
    private boolean active;
    @Embedded
    private Audit audit;
}
