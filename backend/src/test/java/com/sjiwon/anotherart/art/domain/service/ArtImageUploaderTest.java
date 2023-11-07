package com.sjiwon.anotherart.art.domain.service;

import com.sjiwon.anotherart.art.domain.model.UploadImage;
import com.sjiwon.anotherart.common.UseCaseTest;
import com.sjiwon.anotherart.common.mock.stub.StubFileUploader;
import com.sjiwon.anotherart.file.application.adapter.FileUploader;
import com.sjiwon.anotherart.file.domain.model.RawFileData;
import com.sjiwon.anotherart.file.utils.converter.FileConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.sjiwon.anotherart.common.utils.FileMockingUtils.createSingleMockMultipartFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Art -> ArtImageUploader 테스트")
public class ArtImageUploaderTest extends UseCaseTest {
    private final FileUploader fileUploader = new StubFileUploader();
    private final ArtImageUploader sut = new ArtImageUploader(fileUploader);

    private RawFileData file;

    @BeforeEach
    void setUp() throws IOException {
        file = FileConverter.convertImageFile(createSingleMockMultipartFile("1.png", "image/png"));
    }

    @Test
    @DisplayName("파일 이미지를 업로드한다")
    void uploadImage() {
        // when
        final UploadImage uploadImage = sut.uploadImage(file);

        // then
        assertAll(
                () -> assertThat(uploadImage.getUploadFileName()).isEqualTo("1.png"),
                () -> assertThat(uploadImage.getLink()).isEqualTo("S3/" + file.fileName())
        );
    }
}