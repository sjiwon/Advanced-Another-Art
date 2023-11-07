package com.sjiwon.anotherart.art.domain.service;

import com.sjiwon.anotherart.art.domain.model.UploadImage;
import com.sjiwon.anotherart.file.application.adapter.FileUploader;
import com.sjiwon.anotherart.file.domain.model.RawFileData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArtImageUploader {
    private final FileUploader fileUploader;

    public UploadImage uploadImage(final RawFileData file) {
        return new UploadImage(file.fileName(), fileUploader.uploadFile(file));
    }
}
