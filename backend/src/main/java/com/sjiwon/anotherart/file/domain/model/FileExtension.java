package com.sjiwon.anotherart.file.domain.model;

import com.sjiwon.anotherart.file.exception.FileException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static com.sjiwon.anotherart.file.exception.FileExceptionCode.INVALID_FILE_EXTENSION;

@Getter
@RequiredArgsConstructor
public enum FileExtension {
    JPG(".jpg"),
    JPEG(".jpeg"),
    PNG(".png"),
    ;

    private final String value;

    public static FileExtension getExtensionFromFileName(final String fileName) {
        final String fileExtension = extractFileExtension(fileName);

        return Arrays.stream(values())
                .filter(extension -> extension.value.equals(fileExtension))
                .findFirst()
                .orElseThrow(() -> new FileException(INVALID_FILE_EXTENSION));
    }

    public static boolean isValidExtension(final String fileName) {
        final String fileExtension = extractFileExtension(fileName);

        return Arrays.stream(values())
                .anyMatch(extension -> extension.value.equals(fileExtension));
    }

    private static String extractFileExtension(final String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
