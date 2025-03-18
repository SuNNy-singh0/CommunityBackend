package chat.service;

import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.FileInputStream;
import java.io.IOException;

import java.net.URL;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class GoogleCloudStorageService {
    private static final Logger logger = LoggerFactory.getLogger(GoogleCloudStorageService.class);

    @Value("${gcp.bucket.name}")
    private String bucketName;

    @Value("${gcp.project.id}")
    private String projectId;

    @Value("${gcp.credentials.path}")
    private String credentialsPath;

    private final Storage storage;

    public GoogleCloudStorageService(@Value("${gcp.credentials.path}") String credentialsPath,
                                   @Value("${gcp.project.id}") String projectId) {
        try {
            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsPath));
            this.storage = StorageOptions.newBuilder()
                    .setProjectId(projectId)
                    .setCredentials(credentials)
                    .build()
                    .getService();
            logger.info("Successfully initialized Google Cloud Storage service");
        } catch (IOException e) {
            logger.error("Failed to initialize Google Cloud Storage service", e);
            throw new RuntimeException("Failed to initialize storage service", e);
        }
    }

    public String uploadFile(MultipartFile file, String contentType) {
        try {
            logger.info("Starting file upload: {}", file.getOriginalFilename());
            
            String fileName = generateFileName(file);
            BlobId blobId = BlobId.of(bucketName, fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(contentType)
                    .build();

            // Upload the file to Google Cloud Storage
            Blob blob = storage.create(blobInfo, file.getBytes());
            logger.info("File uploaded successfully: {}", fileName);

            // Generate a signed URL that expires in 7 days
            URL signedUrl = storage.signUrl(blobInfo, 7, TimeUnit.DAYS, Storage.SignUrlOption.withV4Signature());
            logger.info("Generated signed URL for file: {}", fileName);

            return signedUrl.toString();
        } catch (Exception e) {
            logger.error("Failed to upload file: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Failed to upload file to Google Cloud Storage: " + e.getMessage(), e);
        }
    }

    private String generateFileName(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }

    public void deleteFile(String fileName) {
        try {
            logger.info("Attempting to delete file: {}", fileName);
            BlobId blobId = BlobId.of(bucketName, fileName);
            boolean deleted = storage.delete(blobId);
            if (deleted) {
                logger.info("File deleted successfully: {}", fileName);
            } else {
                logger.warn("File not found or could not be deleted: {}", fileName);
            }
        } catch (Exception e) {
            logger.error("Failed to delete file: {}", fileName, e);
            throw new RuntimeException("Failed to delete file from Google Cloud Storage: " + e.getMessage(), e);
        }
    }
} 