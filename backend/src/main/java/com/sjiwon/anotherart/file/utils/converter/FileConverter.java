package com.sjiwon.anotherart.file.utils.converter;

import com.sjiwon.anotherart.file.domain.model.FileExtension;
import com.sjiwon.anotherart.file.domain.model.RawFileData;
import com.sjiwon.anotherart.file.exception.FileException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.sjiwon.anotherart.file.exception.FileExceptionCode.FILE_IS_NOT_UPLOAD;

public class FileConverter {
    public static RawFileData convertImageFile(final MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileException(FILE_IS_NOT_UPLOAD);
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
