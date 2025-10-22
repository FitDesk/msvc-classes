package com.classes.services.Impl;

import com.classes.dtos.Trainer.ImageResponseDTO;
import com.classes.exceptions.ImageUploadException;
import com.classes.exceptions.InvalidImageFormatException;
import com.classes.services.CloudinaryService;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    private static final String TRAINERS_FOLDER = "fitdesk/classes/trainerprofile";
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg","jpeg","png","webp");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @Override
    public ImageResponseDTO uploadProfileImage(MultipartFile file, UUID trainerId) {
        log.info("Subiendo imagen de perfil para trainer: {}", trainerId);
        validateImage(file);

        try {
            String publicId = generatePublicIdForTrainer(trainerId);

            Transformation transformation = new Transformation()
                    .width(400)
                    .height(400)
                    .crop("fill")
                    .gravity("face")
                    .quality("auto:good")
                    .fetchFormat("auto");

            Map<String,Object> uploadParams = ObjectUtils.asMap(
                    "public_id", publicId,
                    "folder", TRAINERS_FOLDER,
                    "transformation", transformation,
                    "overwrite", true,
                    "resource_type", "image"
            );

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);

            return mapToResponseDto(uploadResult);
        } catch (IOException e) {
            log.error("Error al subir la imagen de trainer {}: {}", trainerId, e.getMessage());
            throw new ImageUploadException("Error al cargar la imagen", e);
        }
    }

    @Override
    public boolean deleteImage(String publicId) {
        if (publicId == null || publicId.isBlank()) return false;

        try {
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            return "ok".equals(result.get("result"));
        } catch (IOException e) {
            log.error("Error al eliminar la imagen {}: {}", publicId, e.getMessage());
            return false;
        }
    }

    @Override
    public ImageResponseDTO updateProfileImage(MultipartFile file, UUID trainerId, String oldPublicId) {
        log.info("Actualizando imagen de trainer {}", trainerId);
        if (oldPublicId != null && !oldPublicId.isBlank()) {
            deleteImage(oldPublicId);
        }
        return uploadProfileImage(file, trainerId);
    }

    @Override
    public String extractPublicIdFromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return null;
        try {
            String[] parts = imageUrl.split("/upload/");
            if (parts.length > 1) {
                String pathWithVersion = parts[1];
                String path = pathWithVersion.replaceFirst("v\\d+/", "");
                int lastDot = path.lastIndexOf('.');
                return lastDot > 0 ? path.substring(0,lastDot) : path;
            }
        } catch (Exception e) {
            log.warn("No se pudo extraer publicId de: {}", imageUrl);
        }
        return null;
    }

    private String generatePublicIdForTrainer(UUID trainerId) {
        return String.format("%s_%s", trainerId, System.currentTimeMillis());
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidImageFormatException("El archivo está vacío");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidImageFormatException("Archivo excede 5MB");
        }
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (extension == null || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new InvalidImageFormatException("Formato no permitido: "+String.join(", ", ALLOWED_EXTENSIONS));
        }
    }

    private ImageResponseDTO mapToResponseDto(Map uploadResult) {
        return ImageResponseDTO.builder()
                .fileUrl((String) uploadResult.get("secure_url"))
                .fileId((String) uploadResult.get("public_id"))
                .fileName((String) uploadResult.get("original_filename"))
                .format((String) uploadResult.get("format"))
                .size(((Number) uploadResult.get("bytes")).longValue())
                .width((Integer) uploadResult.get("width"))
                .height((Integer) uploadResult.get("height"))
                .build();
    }
}

