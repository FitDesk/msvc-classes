package com.classes.dtos;


import lombok.Data;

@Data
public class LocationDTO {
    private String name;
    private String description;
    private int ability;
    private boolean active;
}
