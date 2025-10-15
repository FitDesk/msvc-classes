package com.classes.services.Impl;
import com.classes.dtos.reservations.ClassReservationRequest;
import com.classes.dtos.reservations.ClassReservationResponse;
import com.classes.entities.ClassEntity;
import com.classes.entities.ClassReservation;
import com.classes.enums.ReservationStatus;
import com.classes.mappers.ClassReservationMapper;
import com.classes.repositories.ClassRepository;
import com.classes.repositories.ClassReservationRepository;
import com.classes.services.ClassReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ClassReservationServiceImpl implements ClassReservationService {

    private final ClassReservationRepository reservationRepository;
    private final ClassRepository classRepository;
    private final ClassReservationMapper mapper;

    @Override
    public ClassReservationResponse reserveClass(ClassReservationRequest request, UUID memberId) {
        // 1️⃣ Obtener la clase
        ClassEntity classEntity = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new RuntimeException("Clase no encontrada"));

        // 2️⃣ Verificar si el usuario ya tiene reserva activa
        Optional<ClassReservation> existing = reservationRepository
                .findByClassEntityIdAndMemberId(classEntity.getId(), memberId);
        if (existing.isPresent() && existing.get().getStatus() != ReservationStatus.CANCELADO) {
            throw new RuntimeException("Ya tienes una reserva para esta clase");
        }

        // 3️⃣ Calcular estado de la reserva (RESERVADO o LISTA_ESPERA)
        long reservedCount = classEntity.getReservations().stream()
                .filter(r -> r.getStatus() == ReservationStatus.RESERVADO)
                .count();
        ReservationStatus status = reservedCount < classEntity.getMaxCapacity()
                ? ReservationStatus.RESERVADO
                : ReservationStatus.LISTA_ESPERA;

        // 4️⃣ Crear la reserva
        ClassReservation reservation = mapper.toEntity(request);
        reservation.setClassEntity(classEntity);
        reservation.setMemberId(memberId);
        reservation.setStatus(status);

        reservationRepository.save(reservation);

        return mapper.toResponse(reservation);
    }

    @Override
    public void cancelReservation(UUID reservationId, UUID memberId) {
        ClassReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        if (!reservation.getMemberId().equals(memberId)) {
            throw new RuntimeException("No puedes cancelar la reserva de otro usuario");
        }

        reservation.setStatus(ReservationStatus.CANCELADO);
        reservationRepository.save(reservation);

        // 5️⃣ Lista de espera automática: pasar al primer miembro en espera
        List<ClassReservation> waitingList = reservationRepository.findByClassEntityId(reservation.getClassEntity().getId())
                .stream()
                .filter(r -> r.getStatus() == ReservationStatus.LISTA_ESPERA)
                .sorted(Comparator.comparing(ClassReservation::getReservedAt))
                .toList();

        if (!waitingList.isEmpty()) {
            ClassReservation firstInLine = waitingList.get(0);
            firstInLine.setStatus(ReservationStatus.RESERVADO);
            reservationRepository.save(firstInLine);
        }
    }

    @Override
    public List<ClassReservationResponse> getReservationsByMember(UUID memberId, Boolean completed) {
        List<ClassReservation> reservations = reservationRepository.findByMemberId(memberId);

        return reservations.stream()
                .filter(r -> completed == null
                        || r.getClassEntity().getStartTime().isBefore(java.time.LocalTime.now()) == completed)
                .sorted(Comparator.comparing(r -> r.getClassEntity().getStartTime()))
                .map(mapper::toResponse)
                .toList();
    }
}
