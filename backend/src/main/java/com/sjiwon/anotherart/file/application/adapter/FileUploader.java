package com.sjiwon.anotherart.file.application.adapter;

import com.sjiwon.anotherart.file.domain.model.RawFileData;

public interface FileUploader {
    String uploadFile(final RawFileData file);
}
