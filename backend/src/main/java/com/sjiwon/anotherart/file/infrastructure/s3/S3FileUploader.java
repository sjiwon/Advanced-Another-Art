package com.sjiwon.anotherart.file.infrastructure.s3;

import com.sjiwon.anotherart.file.application.adapter.FileUploader;
import com.sjiwon.anotherart.file.domain.model.FileExtension;
import com.sjiwon.anotherart.file.domain.model.RawFileData;
import com.sjiwon.anotherart.file.exception.FileException;
import com.sjiwon.anotherart.global.exception.GlobalException;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static com.sjiwon.anotherart.file.exception.FileExceptionCode.FILE_IS_NOT_UPLOAD;
import static com.sjiwon.anotherart.file.infrastructure.s3.BucketMetadata.ART_IMAGES;
import static com.sjiwon.anotherart.global.exception.GlobalExceptionCode.UNEXPECTED_SERVER_ERROR;

@Slf4j
@Component
public class S3FileUploader implements FileUploader {
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

    @Override
    public String uploadFile(final RawFileData file) {
        validateFileExists(file);
        return sendFileToS3(file);
    }

    private void validateFileExists(final RawFileData file) {
        if (file == null) {
            throw new FileException(FILE_IS_NOT_UPLOAD);
        }
    }

    private String sendFileToS3(final RawFileData file) {
        try (final InputStream inputStream = file.content()) {
            final ObjectMetadata objectMetadata = ObjectMetadata.builder()
                    .contentType(file.contenType())
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();
            final String uploadFileName = createFileName(file.extension());

            final String uploadUrlPath = s3Template.upload(bucket, uploadFileName, inputStream, objectMetadata)
                    .getURL()
                    .getPath();
            return cloudFrontUrl + uploadUrlPath;
        } catch (final IOException e) {
            log.error("S3 파일 업로드에 실패했습니다. {}", e.getMessage(), e);
            throw new GlobalException(UNEXPECTED_SERVER_ERROR);
        }
    }

    private String createFileName(final FileExtension fileExtension) {
        final String fileName = UUID.randomUUID() + fileExtension.getValue();
        return String.format(ART_IMAGES, fileName);
    }
}
