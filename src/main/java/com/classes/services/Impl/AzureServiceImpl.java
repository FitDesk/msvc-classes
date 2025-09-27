package com.classes.services.Impl;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.common.StorageSharedKeyCredential;
import com.classes.dtos.FileResponseDTO;
import com.classes.services.AzureService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public class AzureServiceImpl implements AzureService {
    private final BlobContainerClient containerClient;

    public AzureServiceImpl(
            @Value("${azure.storage.account-name}") String accountName,
            @Value("${azure.storage.account-key}") String accountKey,
            @Value("${azure.storage.container-name}") String containerName) {

        String endpoint = String.format("https://%s.blob.core.windows.net", accountName);
        BlobServiceClient serviceClient = new BlobServiceClientBuilder()
                .endpoint(endpoint)
                .credential(new StorageSharedKeyCredential(accountName, accountKey))
                .buildClient();

        this.containerClient = serviceClient.getBlobContainerClient(containerName);
        if (!containerClient.exists()) containerClient.create();
    }
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
