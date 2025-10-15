package com.classes.services;



import com.classes.dtos.reservations.ClassReservationRequest;
import com.classes.dtos.reservations.ClassReservationResponse;
import java.util.List;
import java.util.UUID;

public interface ClassReservationService {
    ClassReservationResponse reserveClass(ClassReservationRequest request, UUID memberId);
    void cancelReservation(UUID reservationId, UUID memberId);
    List<ClassReservationResponse> getReservationsByMember(UUID memberId, Boolean completed);
}

