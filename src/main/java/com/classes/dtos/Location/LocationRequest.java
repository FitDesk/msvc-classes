package com.classes.dtos.Location;

import lombok.Data;

@Data
public class LocationRequest {
    private String name;
    private String description;
    private int ability;
    private boolean active;
}
