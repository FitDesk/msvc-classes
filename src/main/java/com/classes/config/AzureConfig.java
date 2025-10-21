package com.classes.config;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.PublicAccessType;
import com.azure.storage.common.StorageSharedKeyCredential;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureConfig {

    @Value("${azure.storage.account-name}")
    private String accountName;

    @Value("${azure.storage.account-key}")
    private String accountKey;

    @Value("${azure.storage.container-name}")
    private String containerName;

    @Bean
    public BlobContainerClient blobContainerClient() {
        String endpoint = String.format("https://%s.blob.core.windows.net", accountName);
        StorageSharedKeyCredential credential = new StorageSharedKeyCredential(accountName, accountKey);

        BlobServiceClient serviceClient = new BlobServiceClientBuilder()
                .endpoint(endpoint)
                .credential(credential)
                .buildClient();

        BlobContainerClient containerClient = serviceClient.getBlobContainerClient(containerName);

        // Crear el contenedor si no existe y configurar acceso público
        if (!containerClient.exists()) {
            containerClient.create();
            containerClient.setAccessPolicy(PublicAccessType.BLOB, null);
        }

        return containerClient;
    }
}