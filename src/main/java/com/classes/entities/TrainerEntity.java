package com.classes.entities;

import com.classes.enums.ContractType;
import com.classes.enums.DayAvailability;
import com.classes.enums.Gender;
import com.classes.enums.TrainerStatus;
import com.classes.config.Audit;
import com.classes.config.AuditListener;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
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

    private String firstName;
    private String lastName;
    private String dni;
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String phone;
    private String email;
    private String address;
    private String profileImageUrl;

    private String specialties;
    private int yearsOfExperience;

    @ElementCollection
    @CollectionTable(name = "trainer_certifications", joinColumns = @JoinColumn(name = "trainer_id"))
    @Column(name = "certification_url")
    private List<String> certifications;

    @ElementCollection(targetClass = DayAvailability.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "trainer_availability", joinColumns = @JoinColumn(name = "trainer_id"))
    @Column(name = "day")
    private Set<DayAvailability> availability;

    private LocalDate hireDate;

    @Enumerated(EnumType.STRING)
    private TrainerStatus status;

    @Enumerated(EnumType.STRING)
    private ContractType contractType;

    private BigDecimal salaryPerClass;
    private String bankInfo;
    private String notes;

    @Embedded
    private Audit audit;



}