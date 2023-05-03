package com.sjiwon.anotherart.upload.utils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.upload.exception.UploadErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static com.sjiwon.anotherart.upload.utils.BucketMetadata.ART_IMAGES;

@Slf4j
@Component
public class FileUploader {
    private final AmazonS3 amazonS3;
    private final String bucket;

    public FileUploader(AmazonS3 amazonS3,
                        @Value("${cloud.ncp.storage.bucket}") String bucket) {
        this.amazonS3 = amazonS3;
        this.bucket = bucket;
    }

    // 작품 이미지 업로드
    public String uploadArtImage(MultipartFile file) {
        validateFileExists(file);
        return uploadFile(file);
    }

    private void validateFileExists(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw AnotherArtException.type(UploadErrorCode.FILE_IS_EMPTY);
        }
    }

    private String uploadFile(MultipartFile file) {
        String fileName = createFileName(file.getOriginalFilename());

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());

        try (InputStream inputStream = file.getInputStream()) {
            amazonS3.putObject(
                    new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead)
            );
        } catch (IOException e) {
            log.error("S3 파일 업로드에 실패했습니다. {}", e.getMessage());
            throw AnotherArtException.type(UploadErrorCode.S3_UPLOAD_FAILURE);
        }

        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private String createFileName(String originalFileName) {
        String fileName = UUID.randomUUID() + "-" + originalFileName;
        return String.format(ART_IMAGES, fileName);
    }
}
