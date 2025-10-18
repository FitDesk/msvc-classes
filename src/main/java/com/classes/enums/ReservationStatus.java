package com.classes.enums;

public enum ReservationStatus {
    RESERVADO,        // El usuario reservó una clase
    LISTA_ESPERA,     // No hay cupos, está en espera
    CANCELADO,        // El usuario canceló
    PENDIENTE,        // Confirmó que asistirá
    COMPLETADO
}
