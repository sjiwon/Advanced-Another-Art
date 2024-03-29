package com.sjiwon.anotherart.common.utils;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;

public class FileVirtualCreator {
    private static final String FILE_PATH = "src/test/resources/files/";

    public static MultipartFile createFile(final String fileName, final String contentType) {
        try (final FileInputStream stream = new FileInputStream(FILE_PATH + fileName)) {
            return new MockMultipartFile("file", fileName, contentType, stream);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
