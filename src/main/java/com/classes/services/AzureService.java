package com.classes.services;

import com.classes.dtos.FileResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AzureService {
    FileResponseDTO upload(MultipartFile multipartFile) throws IOException;
    void delete(String id) throws IOException;
}
