package com.sjiwon.anotherart.file.utils.converter;

import com.sjiwon.anotherart.file.domain.model.FileExtension;
import com.sjiwon.anotherart.file.domain.model.RawFileData;
import com.sjiwon.anotherart.file.exception.FileErrorCode;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class FileConverter {
    public static RawFileData convertImageFile(final MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw AnotherArtException.type(FileErrorCode.FILE_IS_NOT_UPLOAD);
        }

        final String fileName = file.getOriginalFilename();

        try {
            return new RawFileData(
                    fileName,
                    file.getContentType(),
                    FileExtension.getExtensionFromFileName(fileName),
                    file.getInputStream()
            );
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
