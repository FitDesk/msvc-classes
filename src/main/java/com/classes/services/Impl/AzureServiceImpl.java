package com.classes.services.Impl;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.common.StorageSharedKeyCredential;
import com.classes.dtos.Trainer.FileResponseDTO;
import com.classes.dtos.Trainer.ImageResponseDTO;
import com.classes.services.AzureService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AzureServiceImpl implements AzureService {
    private final BlobContainerClient containerClient;

    @Override
    public FileResponseDTO upload(MultipartFile multipartFile) throws IOException {
        String blobName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
        BlobClient blobClient = containerClient.getBlobClient(blobName);
        blobClient.upload(multipartFile.getInputStream(), multipartFile.getSize(), true);

        return new FileResponseDTO(
                multipartFile.getOriginalFilename(),
                blobClient.getBlobUrl(),
                blobName
        );
    }

    @Override
    public void delete(String blobName) throws IOException {
        BlobClient blobClient = containerClient.getBlobClient(blobName);
        blobClient.deleteIfExists();
    }
}
