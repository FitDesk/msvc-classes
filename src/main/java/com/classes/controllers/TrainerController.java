package com.classes.controllers;

import com.classes.annotations.AdminAccess;
import com.classes.annotations.AdminOrTrainerAccess;
import com.classes.dtos.TrainerDTO;
import com.classes.services.TrainerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


import java.util.UUID;

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;

    @AdminAccess
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TrainerDTO> createTrainer(
            @RequestParam("trainer") String trainerJson,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestParam(value = "certifications", required = false) List<MultipartFile> certifications
    ) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        TrainerDTO trainerDTO = mapper.readValue(trainerJson, TrainerDTO.class);
        TrainerDTO createdTrainer = trainerService.createTrainer(trainerDTO, profileImage, certifications);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTrainer);
    }

    @PreAuthorize("@authService.canAccessResource(#id, authentication)")
    @GetMapping("/{id}")
    public ResponseEntity<TrainerDTO> getTrainerById(@PathVariable UUID id) {
        TrainerDTO trainer = trainerService.getTrainerById(id);
        return ResponseEntity.ok(trainer);
    }

    @AdminAccess
    @GetMapping
    public ResponseEntity<List<TrainerDTO>> getAllTrainers() {
        List<TrainerDTO> trainers = trainerService.getAllTrainers();
        return ResponseEntity.ok(trainers);
    }

    @PreAuthorize("@authService.canAccessResource(#ownerId, authentication)")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TrainerDTO> updateTrainer(
            @PathVariable UUID id,
            @RequestParam("trainer") String trainerJson,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestParam(value = "certifications", required = false) List<MultipartFile> certifications
    ) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        TrainerDTO trainerDTO = mapper.readValue(trainerJson, TrainerDTO.class);
        TrainerDTO updatedTrainer = trainerService.updateTrainer(id, trainerDTO, profileImage, certifications);
        return ResponseEntity.ok(updatedTrainer);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrainer(@PathVariable UUID id) {
        try {
            trainerService.deleteTrainer(id);
            return ResponseEntity.noContent().build();
        } catch (
                EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (
                IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

