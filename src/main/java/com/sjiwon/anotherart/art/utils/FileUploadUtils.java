package com.sjiwon.anotherart.art.utils;

import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.global.exception.GlobalErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Component
public class FileUploadUtils {
    private final String fileDir;

    public FileUploadUtils(@Value("${file.dir}") String fileDir) {
        this.fileDir = fileDir;
    }

    public void uploadArtImage(MultipartFile file, String storageName) {
        try {
            file.transferTo(new File(fileDir + storageName));
        } catch (IOException e) {
            throw AnotherArtException.type(GlobalErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
