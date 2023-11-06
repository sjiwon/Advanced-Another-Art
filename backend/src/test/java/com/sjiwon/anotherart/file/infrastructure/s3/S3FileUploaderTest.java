package com.sjiwon.anotherart.file.infrastructure.s3;

import com.sjiwon.anotherart.file.domain.model.RawFileData;
import com.sjiwon.anotherart.file.exception.FileErrorCode;
import com.sjiwon.anotherart.file.utils.converter.FileConverter;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static com.sjiwon.anotherart.common.utils.FileMockingUtils.createSingleMockMultipartFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("File -> S3FileUploader 테스트")
public class S3FileUploaderTest {
    private static final String BUCKET = "bucket";
    private static final String CLOUD_FRONT_URL = "https://cloudfront-domain";

    private final S3Template s3Template = mock(S3Template.class);
    private final S3Resource s3Resource = mock(S3Resource.class);
    private final S3FileUploader sut = new S3FileUploader(s3Template, BUCKET, CLOUD_FRONT_URL);

    private RawFileData rawFileData;
    private String imageUploadLinkPath;

    @BeforeEach
    void setUp() throws IOException {
        rawFileData = FileConverter.convertImageFile(createSingleMockMultipartFile("1.png", "image/png"));
        imageUploadLinkPath = "/" + rawFileData.fileName();
    }

    @Test
    @DisplayName("Client가 파일을 전송하지 않았으면 예외가 발생한다")
    void throwExceptionByFileIsNotUpload() {
        assertThatThrownBy(() -> sut.uploadFile(null))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(FileErrorCode.FILE_IS_NOT_UPLOAD.getMessage());

        verify(s3Template, times(0))
                .upload(any(String.class), any(String.class), any(InputStream.class), any(ObjectMetadata.class));
    }

    @Test
    @DisplayName("S3에 파일을 업로드한다")
    void success() throws IOException {
        // given
        final URL mockUrl = new URL("https://s3" + imageUploadLinkPath);
        given(s3Template.upload(
                any(String.class),
                any(String.class),
                any(InputStream.class),
                any(ObjectMetadata.class)
        )).willReturn(s3Resource);
        given(s3Resource.getURL()).willReturn(mockUrl);

        // when
        final String uploadLink = sut.uploadFile(rawFileData);

        // then
        assertAll(
                () -> verify(s3Template, times(1))
                        .upload(any(String.class), any(String.class), any(InputStream.class), any(ObjectMetadata.class)),
                () -> assertThat(uploadLink).isEqualTo(CLOUD_FRONT_URL + imageUploadLinkPath)
        );
    }
}
