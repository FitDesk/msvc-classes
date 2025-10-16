package com.classes.dtos.Trainer;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileResponseDTO {
    private String fileName;
    private String fileUrl;
    private String fileId; // en Cloudinary sería el publicId, en Azure el blobName
}