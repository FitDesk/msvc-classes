package com.classes.controllers;

import com.classes.dtos.Trainer.ImageResponseDTO;
import com.classes.dtos.Trainer.TrainerRequestDTO;
import com.classes.dtos.Trainer.TrainerResponseDTO;
import com.classes.services.AuthorizationService;
import com.classes.services.TrainerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


import java.util.UUID;

@RestController
@RequestMapping("/trainers")
@RequiredArgsConstructor
@Slf4j
public class TrainerController {

    private final TrainerService trainerService;
    private final AuthorizationService authorizationService;

        @PreAuthorize("@authorizationServiceImpl.canAccessResource(#id,authentication)")
        @GetMapping
        public ResponseEntity<Page<TrainerResponseDTO>> getAllTrainers(
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "10") int size,
                @RequestParam(required = false) String search,
                @RequestParam(required = false) String status
        ) {
            Page<TrainerResponseDTO> trainers = trainerService.getAllTrainers(page, size, search, status);
            return ResponseEntity.ok(trainers);
        }

        @PreAuthorize("@authorizationServiceImpl.canAccessResource(#id,authentication)")
        @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<TrainerResponseDTO> createTrainer(
                @RequestParam("trainer") String trainerJson,
                @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
                @RequestParam(value = "certifications", required = false) List<MultipartFile> certifications
        ) throws IOException {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            TrainerRequestDTO trainerDTO = mapper.readValue(trainerJson, TrainerRequestDTO.class);
            TrainerResponseDTO createdTrainer = trainerService.createTrainer(trainerDTO, profileImage, certifications);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTrainer);
        }

        @PreAuthorize("@authorizationServiceImpl.canAccessResource(#id,authentication)")
        @GetMapping("/{id}")
        public ResponseEntity<TrainerResponseDTO> getTrainerById(@PathVariable UUID id) {
            TrainerResponseDTO trainer = trainerService.getTrainerById(id);
            return ResponseEntity.ok(trainer);
        }


        @PreAuthorize("@authorizationServiceImpl.canAccessResource(#id,authentication)")
        @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<TrainerResponseDTO> updateTrainer(
                @PathVariable UUID id,
                @RequestParam("trainer") String trainerJson,
                @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
                @RequestParam(value = "certifications", required = false) List<MultipartFile> certifications
        ) throws IOException {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            TrainerRequestDTO trainerDTO = mapper.readValue(trainerJson, TrainerRequestDTO.class);
            TrainerResponseDTO updatedTrainer = trainerService.updateTrainer(id, trainerDTO, profileImage, certifications);
            return ResponseEntity.ok(updatedTrainer);
        }


        @PreAuthorize("hasRole('TRAINER')")
        @PutMapping("/{id}/profile-image")
        public ResponseEntity<ImageResponseDTO> updateTrainerProfileImage(
                @RequestParam("file") MultipartFile file,
                Authentication authentication) throws IOException {
            UUID trainerId = authorizationService.getUserId(authentication);

            ImageResponseDTO response = trainerService.updateTrainerProfile(trainerId, file);
            return ResponseEntity.ok(response);
        }

        @PreAuthorize("@authorizationServiceImpl.canAccessResource(#id,authentication)")
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteTrainer(@PathVariable UUID id) {
            try {
                trainerService.deleteTrainer(id);
                return ResponseEntity.noContent().build(); // 204 No Content si se eliminó correctamente
            } catch (EntityNotFoundException e) {
                return ResponseEntity.notFound().build();
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(null);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }


    @DeleteMapping("/profile-image")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTrainerProfileImage(Authentication authentication) {
        UUID trainerId = authorizationService.getUserId(authentication);
        log.info("Trainer {} eliminando su imagen de perfil", trainerId);
        boolean deleted = trainerService.deleteTrainerProfileImage(trainerId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.badRequest().build();
    }

    }


