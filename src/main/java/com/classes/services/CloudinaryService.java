package com.classes.services;

import com.classes.dtos.Trainer.FileResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CloudinaryService {
    FileResponseDTO upload(MultipartFile multipartFile) throws IOException;
    void delete(String id) throws IOException;
}

