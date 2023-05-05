package com.sjiwon.anotherart.upload.utils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.sjiwon.anotherart.common.InfraTest;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.upload.exception.UploadErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.UUID;

import static com.sjiwon.anotherart.common.utils.FileMockingUtils.createSingleMockMultipartFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Upload [Utils] -> FileUploader 테스트")
class FileUploaderTest extends InfraTest {
    private FileUploader uploader;

    @Mock
    private AmazonS3 amazonS3;
    private static final String BUCKET = "bucket";

    @BeforeEach
    void setUp() {
        uploader = new FileUploader(amazonS3, BUCKET);
    }

    @Nested
    @DisplayName("작품 이미지 업로드")
    class uploadArtImage {
        @Test
        @DisplayName("파일을 전송하지 않았거나 파일의 사이즈가 0이면 업로드가 불가능하다")
        void throwExceptionByFileIsEmpty() {
            // given
            MultipartFile nullFile = null;
            MultipartFile emptyFile = new MockMultipartFile("file", "hello.png", "image/png", new byte[]{});

            // when - then
            assertThatThrownBy(() -> uploader.uploadArtImage(nullFile))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(UploadErrorCode.FILE_IS_EMPTY.getMessage());
            assertThatThrownBy(() -> uploader.uploadArtImage(emptyFile))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(UploadErrorCode.FILE_IS_EMPTY.getMessage());
        }

        @Test
        @DisplayName("이미지 업로드를 성공한다")
        void success() throws Exception {
            // given
            MultipartFile file = createSingleMockMultipartFile("1.png", "image/png");

            PutObjectResult putObjectResult = new PutObjectResult();
            given(amazonS3.putObject(any(PutObjectRequest.class))).willReturn(putObjectResult);

            URL mockUrl = new URL(createUploadLink("hello4.png"));
            given(amazonS3.getUrl(eq(BUCKET), anyString())).willReturn(mockUrl);

            // when
            String uploadUrl = uploader.uploadArtImage(file);

            // then
            verify(amazonS3, times(1)).putObject(any(PutObjectRequest.class));
            verify(amazonS3, times(1)).getUrl(eq(BUCKET), anyString());
            assertThat(uploadUrl).isEqualTo(mockUrl.toString());
        }
    }

    private String createUploadLink(String originalFileName) {
        return String.format(
                "https://kr.object.ncloudstorage.com/bucket/arts/%s-%s",
                UUID.randomUUID(),
                originalFileName
        );
    }
}
