package br.com.ecommerce.ecom.service;

import br.com.ecommerce.ecom.enums.UploadType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public String uploadFile(MultipartFile file, UploadType type, Long entityId, String entityName) throws IOException {
        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null;
        String extension = getFileExtension(originalFilename);
        String sanitizedName = entityName.toLowerCase().replaceAll("[^a-z0-9]+", "-");

        String key = String.format(
                type.name().toLowerCase(), // products, brands, categories
                entityId,
                sanitizedName,
                UUID.randomUUID(),
                extension);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        return String.format("https://%s.s3.amazonaws.com/%s", bucketName, URLEncoder.encode(key, StandardCharsets.UTF_8));
    }


    public void deleteFile(String key) {
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteRequest);
        log.info("Deleted file from S3 with key: {}", key);
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex >= 0) ? filename.substring(dotIndex + 1) : "";
    }

    public String extractKeyFromUrl(String imageUrl) {
        String bucketBaseUrl = "https://" + bucketName + ".s3.amazonaws.com/";
        if (!imageUrl.startsWith(bucketBaseUrl)) {
            throw new IllegalArgumentException("Invalid image URL: " + imageUrl);
        }
        String rawKey = imageUrl.substring(bucketBaseUrl.length());
        return URLDecoder.decode(rawKey, StandardCharsets.UTF_8);
    }

}
