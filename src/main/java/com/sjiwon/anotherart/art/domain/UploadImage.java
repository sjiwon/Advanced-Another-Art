package com.sjiwon.anotherart.art.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class UploadImage {
    @Column(name = "upload_name", nullable = false, length = 200)
    private String uploadName;

    @Column(name = "storage_name", nullable = false, unique = true, length = 40)
    private String storageName;

    private UploadImage(String uploadName, String storageName) {
        this.uploadName = uploadName;
        this.storageName = storageName;
    }

    public static UploadImage of(String uploadName, String storageName) {
        return new UploadImage(uploadName, storageName);
    }
}
