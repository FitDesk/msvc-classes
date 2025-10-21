package com.classes.entities;

import com.classes.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "class_reservations")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private ClassEntity classEntity;

    private UUID memberId;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status; // RESERVADO, LISTA_ESPERA, CANCELADO
    private LocalDateTime reservedAt;
    @Column(name = "attended")
    private Boolean attended = false;



}
