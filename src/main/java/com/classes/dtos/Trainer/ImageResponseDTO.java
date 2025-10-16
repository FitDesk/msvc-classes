package com.classes.dtos.Trainer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageResponseDTO {
    private String fileName;
    private String fileUrl;
    private String fileId; // publicId en Cloudinary
    private String format;
    private Long size;
    private Integer width;
    private Integer height;

}