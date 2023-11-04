package com.sjiwon.anotherart.upload.utils;

import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.upload.exception.UploadErrorCode;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static com.sjiwon.anotherart.upload.utils.BucketMetadata.ART_IMAGES;

@Slf4j
@Component
public class S3FileUploader {
    private final S3Template s3Template;
    private final String bucket;
    private final String cloudFrontUrl;

    public S3FileUploader(
            final S3Template s3Template,
            @Value("${spring.cloud.aws.s3.bucket}") final String bucket,
            @Value("${spring.cloud.aws.cloudfront.url}") final String cloudFrontUrl
    ) {
        this.s3Template = s3Template;
        this.bucket = bucket;
        this.cloudFrontUrl = cloudFrontUrl;
    }

    // TODO RawFileData Domain으로 수정
    public String uploadFile(final MultipartFile file) {
        validateFileExists(file);
        return sendFileToS3(file);
    }

    private void validateFileExists(final MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw AnotherArtException.type(UploadErrorCode.FILE_IS_EMPTY);
        }
    }

    private String sendFileToS3(final MultipartFile file) {
        try (final InputStream inputStream = file.getInputStream()) {
            final ObjectMetadata objectMetadata = ObjectMetadata.builder()
                    .contentType(file.getContentType())
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();
            final String uploadFileName = createFileName(file.getOriginalFilename());

            final String uploadUrlPath = s3Template.upload(bucket, uploadFileName, inputStream, objectMetadata)
                    .getURL()
                    .getPath();
            return cloudFrontUrl + uploadUrlPath;
        } catch (final IOException e) {
            log.error("S3 파일 업로드에 실패했습니다. {}", e.getMessage(), e);
            throw AnotherArtException.type(UploadErrorCode.S3_UPLOAD_FAILURE);
        }
    }

    private String createFileName(final String originalFileName) {
        final String fileName = UUID.randomUUID() + originalFileName.substring(originalFileName.lastIndexOf("."));
        return String.format(ART_IMAGES, fileName);
    }
}
