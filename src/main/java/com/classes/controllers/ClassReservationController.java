package com.classes.controllers;

import com.classes.dtos.reservations.ClassReservationRequest;
import com.classes.dtos.reservations.ClassReservationResponse;
import com.classes.services.AuthorizationService;
import com.classes.services.ClassReservationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
@Slf4j
public class ClassReservationController {

    private final ClassReservationService reservationService;
    private final AuthorizationService authorizationService;


    @Operation(summary = "Reservar una clase")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ClassReservationResponse> reserveClass(
            @RequestBody ClassReservationRequest request,
            Authentication authentication) {

        UUID memberId = authorizationService.getUserId(authentication);
        log.info("🎟️ Usuario {} intentando reservar clase {}", memberId, request.getClassId());

        ClassReservationResponse response = reservationService.reserveClass(request, memberId);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Cancelar una reserva")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable UUID reservationId,
            Authentication authentication) {

        UUID memberId = authorizationService.getUserId(authentication);
        log.info("❌ Usuario {} cancelando reserva {}", memberId, reservationId);

        reservationService.cancelReservation(reservationId, memberId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtener mis reservas activas o completadas")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/my")
    public ResponseEntity<List<ClassReservationResponse>> getMyReservations(
            Authentication authentication,
            @RequestParam(required = false) Boolean completed) {

        UUID memberId = authorizationService.getUserId(authentication);
        log.info("📋 Usuario {} consultando sus reservas", memberId);

        List<ClassReservationResponse> reservations = reservationService.getReservationsByMember(memberId, completed);
        if (reservations.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(reservations);
    }
}