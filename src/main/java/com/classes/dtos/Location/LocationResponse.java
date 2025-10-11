package com.classes.dtos.Location;

import lombok.Data;

import java.util.UUID;

@Data
public class LocationResponse {
    private UUID id; // si tienes un ID generado
    private String name;
    private String description;
    private int ability;
    private boolean active;
}