package com.classes.services.Impl;

import com.classes.dtos.Trainer.FileResponseDTO;
import com.classes.dtos.Trainer.TrainerDTO;
import com.classes.entities.TrainerEntity;
import com.classes.mappers.TrainerMapper;
import com.classes.repositories.ClassRepository;
import com.classes.repositories.TrainerRepository;
import com.classes.services.AzureService;
import com.classes.services.CloudinaryService;
import com.classes.services.TrainerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerRepository;
    private final TrainerMapper trainerMapper;
    private final CloudinaryService cloudinaryService;
    private final AzureService azureStorageService;
    private final ClassRepository classRepository;

    @Transactional
    @Override
    public TrainerDTO createTrainer(TrainerDTO trainerDTO,
                                    MultipartFile profileImage,
                                    List<MultipartFile> certifications) throws IOException {

        TrainerEntity trainer = trainerMapper.toEntity(trainerDTO);
        if (profileImage != null && !profileImage.isEmpty()) {
            FileResponseDTO profileFile = cloudinaryService.upload(profileImage);
            trainer.setProfileImageUrl(profileFile.getFileUrl());
        }
        if (certifications != null && !certifications.isEmpty()) {
            List<String> certUrls = new ArrayList<>();
            for (MultipartFile cert : certifications) {
                FileResponseDTO fileResponse = azureStorageService.upload(cert);
                certUrls.add(fileResponse.getFileUrl());
            }
            trainer.setCertifications(certUrls);
        }
        TrainerEntity savedTrainer = trainerRepository.save(trainer);
        return trainerMapper.toDTO(savedTrainer);
    }

    @Transactional(readOnly = true)
    @Override
    public TrainerDTO getTrainerById(UUID id) {
        TrainerEntity trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found with id: " + id));
        return trainerMapper.toDTO(trainer);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TrainerDTO> getAllTrainers() {
        return trainerMapper.toDTOList(trainerRepository.findAll());
    }

    @Transactional
    @Override
    public TrainerDTO updateTrainer(UUID id,
                                    TrainerDTO trainerDTO,
                                    MultipartFile profileImage,
                                    List<MultipartFile> certifications) throws IOException {

        TrainerEntity trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found with id: " + id));
        trainerMapper.updateTrainerFromDTO(trainerDTO, trainer);
        if (profileImage != null && !profileImage.isEmpty()) {
            FileResponseDTO profileFile = cloudinaryService.upload(profileImage);
            trainer.setProfileImageUrl(profileFile.getFileUrl());
        }
        if (certifications != null && !certifications.isEmpty()) {
            List<String> urls = new ArrayList<>();
            for (MultipartFile cert : certifications) {
                FileResponseDTO fileResponse = azureStorageService.upload(cert);
                urls.add(fileResponse.getFileUrl());
            }
            trainer.setCertifications(urls);
        }

        TrainerEntity updated = trainerRepository.save(trainer);
        return trainerMapper.toDTO(updated);
    }
    @Transactional
    @Override
    public void deleteTrainer(UUID id) throws IOException {
        TrainerEntity trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found with id: " + id));

        boolean hasClasses = classRepository.findFirstByTrainerId(id).isPresent();
        if (hasClasses) {
            throw new IllegalArgumentException(
                    "No se puede eliminar el trainer porque tiene clases asignadas"
            );
        }
        if (trainer.getProfileImageUrl() != null) {
            cloudinaryService.delete(trainer.getProfileImageUrl());
        }
        if (trainer.getCertifications() != null) {
            for (String url : trainer.getCertifications()) {
                String blobName = url.substring(url.lastIndexOf("/") + 1);
                azureStorageService.delete(blobName);
            }
        }
        trainerRepository.delete(trainer);
    }
}