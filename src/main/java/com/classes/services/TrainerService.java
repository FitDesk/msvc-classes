package com.classes.services;

import com.classes.dtos.Trainer.ImageResponseDTO;
import com.classes.dtos.Trainer.TrainerRequestDTO;
import com.classes.dtos.Trainer.TrainerResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface TrainerService {
    TrainerResponseDTO createTrainer(TrainerRequestDTO trainerDTO,
                                     MultipartFile profileImage,
                                     List<MultipartFile> certifications) throws IOException;


    TrainerResponseDTO getTrainerById(UUID id);


    Page<TrainerResponseDTO> getAllTrainers(int page, int size, String search, String status);


    TrainerResponseDTO updateTrainer(UUID id,
                                     TrainerRequestDTO trainerDTO,
                                     MultipartFile profileImage,
                                     List<MultipartFile> certifications) throws IOException;


    void deleteTrainer(UUID id) throws IOException;


    ImageResponseDTO updateTrainerProfile(UUID trainerId, MultipartFile file) throws IOException;
    boolean deleteTrainerProfileImage(UUID trainerId) ;
}




