package com.sjiwon.anotherart.common.mock.stub;

import com.sjiwon.anotherart.file.application.adapter.FileUploader;
import com.sjiwon.anotherart.file.domain.model.RawFileData;

public class StubFileUploader implements FileUploader {
    @Override
    public String uploadFile(final RawFileData file) {
        return "S3/" + file.fileName();
    }
}
