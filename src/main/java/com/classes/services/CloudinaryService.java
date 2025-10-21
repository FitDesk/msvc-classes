package com.classes.services;

import com.classes.dtos.Trainer.ImageResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface CloudinaryService {
    ImageResponseDTO uploadProfileImage(MultipartFile file, UUID trainerId);

    boolean deleteImage(String publicId);

    ImageResponseDTO updateProfileImage(MultipartFile file, UUID trainerId, String oldPublicId) ;

    String extractPublicIdFromUrl(String imageUrl);
}


