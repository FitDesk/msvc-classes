package com.classes.services.Impl;

import com.classes.dtos.Trainer.FileResponseDTO;
import com.classes.dtos.Trainer.ImageResponseDTO;
import com.classes.dtos.Trainer.TrainerRequestDTO;
import com.classes.dtos.Trainer.TrainerResponseDTO;
import com.classes.entities.TrainerEntity;
import com.classes.enums.TrainerStatus;
import com.classes.mappers.TrainerMapper;
import com.classes.repositories.ClassRepository;
import com.classes.repositories.TrainerRepository;
import com.classes.services.AzureService;
import com.classes.services.CloudinaryService;
import com.classes.services.TrainerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public TrainerResponseDTO createTrainer(TrainerRequestDTO trainerDTO,
                                            MultipartFile profileImage,
                                            List<MultipartFile> certifications) throws IOException {

        // Primero guardamos el trainer para generar el UUID
        TrainerEntity trainer = trainerMapper.toEntity(trainerDTO);
        TrainerEntity savedTrainer = trainerRepository.save(trainer);

        // Subida de imagen de perfil
        if (profileImage != null && !profileImage.isEmpty()) {
            ImageResponseDTO profileFile = cloudinaryService.uploadProfileImage(profileImage, savedTrainer.getId());
            savedTrainer.setProfileImageUrl(profileFile.getFileUrl());
        }

        // Subida de certificaciones
        if (certifications != null && !certifications.isEmpty()) {
            List<String> certUrls = new ArrayList<>();
            for (MultipartFile cert : certifications) {
                FileResponseDTO fileResponse = azureStorageService.upload(cert);
                certUrls.add(fileResponse.getFileUrl());
            }
            savedTrainer.setCertifications(certUrls);
        }

        trainerRepository.save(savedTrainer);
        return trainerMapper.toResponseDTO(savedTrainer);
    }

    @Transactional(readOnly = true)
    @Override
    public TrainerResponseDTO getTrainerById(UUID id) {
        TrainerEntity trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found with id: " + id));
        return trainerMapper.toResponseDTO(trainer);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<TrainerResponseDTO> getAllTrainers(int page, int size, String search, String status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());
        
        if (search != null && !search.trim().isEmpty()) {
       
            return trainerRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(search, search, pageable)
                    .map(trainerMapper::toResponseDTO);
        } else if (status != null && !status.trim().isEmpty()) {
            
            TrainerStatus trainerStatus = TrainerStatus.valueOf(status.toUpperCase());
            return trainerRepository.findByStatus(trainerStatus, pageable)
                    .map(trainerMapper::toResponseDTO);
        } else {
            
            return trainerRepository.findAll(pageable)
                    .map(trainerMapper::toResponseDTO);
        }
    }

    @Transactional
    @Override
    public TrainerResponseDTO updateTrainer(UUID id,
                                            TrainerRequestDTO trainerDTO,
                                            MultipartFile profileImage,
                                            List<MultipartFile> certifications) throws IOException {

        TrainerEntity trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found with id: " + id));

        trainerMapper.updateTrainerFromDTO(trainerDTO, trainer);

        if (profileImage != null && !profileImage.isEmpty()) {
            String oldPublicId = trainer.getProfileImageUrl() != null
                    ? cloudinaryService.extractPublicIdFromUrl(trainer.getProfileImageUrl())
                    : null;

            ImageResponseDTO profileFile = cloudinaryService.updateProfileImage(profileImage, trainer.getId(), oldPublicId);
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
        return trainerMapper.toResponseDTO(updated);
    }

    @Transactional
    @Override
    public void deleteTrainer(UUID id) throws IOException {
        TrainerEntity trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found with id: " + id));

        boolean hasClasses = classRepository.findFirstByTrainerId(id).isPresent();
        if (hasClasses) {
            throw new IllegalArgumentException("No se puede eliminar el trainer porque tiene clases asignadas");
        }

        if (trainer.getProfileImageUrl() != null) {
            String publicId = cloudinaryService.extractPublicIdFromUrl(trainer.getProfileImageUrl());
            cloudinaryService.deleteImage(publicId);
        }

        if (trainer.getCertifications() != null) {
            for (String url : trainer.getCertifications()) {
                String blobName = url.substring(url.lastIndexOf("/") + 1);
                azureStorageService.delete(blobName);
            }
        }

        trainerRepository.delete(trainer);
    }

    @Transactional
    @Override
    public ImageResponseDTO updateTrainerProfile(UUID trainerId, MultipartFile file) {
        TrainerEntity trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found with id: " + trainerId));

        String oldPublicId = cloudinaryService.extractPublicIdFromUrl(trainer.getProfileImageUrl());
        ImageResponseDTO uploadResponse = cloudinaryService.updateProfileImage(file, trainerId, oldPublicId);

        trainer.setProfileImageUrl(uploadResponse.getFileUrl());
        trainerRepository.save(trainer);

        return uploadResponse;
    }

    @Transactional
    @Override
    public boolean deleteTrainerProfileImage(UUID trainerId) {
        TrainerEntity trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found with id: " + trainerId));

        String currentImageUrl = trainer.getProfileImageUrl();
        if (currentImageUrl == null) return false;

        String publicId = cloudinaryService.extractPublicIdFromUrl(currentImageUrl);
        boolean deleted = false;
        if (publicId != null) {
            deleted = cloudinaryService.deleteImage(publicId);
        }

        trainer.setProfileImageUrl(null);
        trainerRepository.save(trainer);

        return deleted;
    }
}