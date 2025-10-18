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

import java.time.LocalDateTime;
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

    // Reservar clase
    @Override
    public ClassReservationResponse reserveClass(ClassReservationRequest request, UUID memberId) {
        ClassEntity classEntity = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new RuntimeException("Clase no encontrada"));

        Optional<ClassReservation> existing = reservationRepository
                .findByClassEntityIdAndMemberId(classEntity.getId(), memberId);

        if (existing.isPresent() && existing.get().getStatus() != ReservationStatus.CANCELADO) {
            throw new RuntimeException("Ya tienes una reserva activa para esta clase");
        }

        long reservedCount = classEntity.getReservations().stream()
                .filter(r -> r.getStatus() == ReservationStatus.RESERVADO)
                .count();

        ReservationStatus status = reservedCount < classEntity.getMaxCapacity()
                ? ReservationStatus.RESERVADO
                : ReservationStatus.LISTA_ESPERA;

        ClassReservation reservation = mapper.toEntity(request);
        reservation.setClassEntity(classEntity);
        reservation.setMemberId(memberId);
        reservation.setStatus(status);
        reservation.setReservedAt(LocalDateTime.now());

        reservationRepository.save(reservation);

        return mapper.toResponse(reservation);
    }

    // Cancelar reserva
    @Override
    public void cancelReservation(UUID reservationId, UUID memberId) {
        ClassReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        if (!reservation.getMemberId().equals(memberId)) {
            throw new RuntimeException("No puedes cancelar la reserva de otro usuario");
        }

        reservation.setStatus(ReservationStatus.CANCELADO);
        reservationRepository.save(reservation);

        // Promover primer usuario en lista de espera
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

    // Confirmar asistencia
    @Override
    public void confirmAttendance(UUID reservationId, UUID memberId) {
        ClassReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        if (!reservation.getMemberId().equals(memberId)) {
            throw new RuntimeException("No puedes confirmar la asistencia de otro usuario");
        }

        if (reservation.getStatus() != ReservationStatus.RESERVADO) {
            throw new RuntimeException("Solo puedes confirmar reservas activas");
        }

        reservation.setStatus(ReservationStatus.PENDIENTE);
        reservationRepository.save(reservation);
    }

    // Marcar clase como completada
    @Override
    public void completeReservation(UUID reservationId, UUID memberId) {
        // 1️⃣ Buscar la reserva
        ClassReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        // 2️⃣ Validar que sea la misma persona (opcional, si solo el usuario puede completar)
        if (!reservation.getMemberId().equals(memberId)) {
            throw new RuntimeException("No puedes completar la reserva de otro usuario");
        }

        // 3️⃣ Verificar si la clase ya terminó
        LocalDateTime classEnd = LocalDateTime.of(
                reservation.getClassEntity().getClassDate(),
                reservation.getClassEntity().getEndTime()
        );

        if (classEnd.isAfter(LocalDateTime.now())) {
            throw new RuntimeException("La clase aún no ha terminado, no se puede completar");
        }

        // 4️⃣ Cambiar el estado a COMPLETADO
        reservation.setStatus(ReservationStatus.COMPLETADO);
        reservationRepository.save(reservation);
    }

    // Listar reservas por miembro
    @Override
    public List<ClassReservationResponse> getReservationsByMember(UUID memberId, Boolean completed) {
        List<ClassReservation> reservations = reservationRepository.findByMemberId(memberId);

        return reservations.stream()
                .filter(r -> {
                    if (completed == null) return true;

                    // Combinar fecha y hora en LocalDateTime
                    LocalDateTime classEnd = LocalDateTime.of(
                            r.getClassEntity().getClassDate(),
                            r.getClassEntity().getEndTime()
                    );

                    return (classEnd.isBefore(LocalDateTime.now()) == completed);
                })
                .sorted(Comparator.comparing(r ->
                        LocalDateTime.of(r.getClassEntity().getClassDate(), r.getClassEntity().getStartTime())
                ))
                .map(mapper::toResponse)
                .toList();
    }
}
