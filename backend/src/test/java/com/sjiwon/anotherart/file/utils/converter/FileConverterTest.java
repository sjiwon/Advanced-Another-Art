package com.sjiwon.anotherart.file.utils.converter;

import com.sjiwon.anotherart.file.domain.model.RawFileData;
import com.sjiwon.anotherart.file.exception.FileErrorCode;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static com.sjiwon.anotherart.common.utils.FileMockingUtils.createSingleMockMultipartFile;
import static com.sjiwon.anotherart.file.domain.model.FileExtension.PNG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("FIle -> FileConverter 테스트")
public class FileConverterTest {
    @Test
    @DisplayName("MultipartFile이 비어있으면 예외가 발생한다")
    void throwExceptionByFIleIsNotUpload() {
        // given
        final MultipartFile file = new MockMultipartFile("hello.png", new byte[0]);

        // when - then
        assertThatThrownBy(() -> FileConverter.convertImageFile(file))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(FileErrorCode.FILE_IS_NOT_UPLOAD.getMessage());
    }

    @Test
    @DisplayName("MultipartFile -> RawFileData로 Converting한다")
    void success() {
        // given
        final MultipartFile file = createSingleMockMultipartFile("1.png", "image/png");

        // when
        final RawFileData rawFileData = FileConverter.convertImageFile(file);

        // then
        assertAll(
                () -> assertThat(rawFileData.fileName()).isEqualTo("1.png"),
                () -> assertThat(rawFileData.contenType()).isEqualTo("image/png"),
                () -> assertThat(rawFileData.extension()).isEqualTo(PNG)
        );
    }
}
