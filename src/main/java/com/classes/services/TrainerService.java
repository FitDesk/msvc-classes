package com.classes.services;

import com.classes.dtos.TrainerDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface TrainerService {
    TrainerDTO createTrainer(TrainerDTO trainerDTO,
                             MultipartFile profileImage,
                             List<MultipartFile> certifications) throws IOException;

    TrainerDTO getTrainerById(UUID id);

    List<TrainerDTO> getAllTrainers();

    TrainerDTO updateTrainer(UUID id,
                             TrainerDTO trainerDTO,
                             MultipartFile profileImage,
                             List<MultipartFile> certifications) throws IOException;

    void deleteTrainer(UUID id) throws IOException;
}

