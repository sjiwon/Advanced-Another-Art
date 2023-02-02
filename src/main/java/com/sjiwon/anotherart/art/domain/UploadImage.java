package com.sjiwon.anotherart.art.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.util.UUID;

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

    public static UploadImage from(MultipartFile file) {
        String originalFileName = getOriginalFileName(file);
        String serverStorageName = generateServerStorageName(originalFileName);
        return new UploadImage(originalFileName, serverStorageName);
    }

    private static String getOriginalFileName(MultipartFile file) {
        return file.getOriginalFilename();
    }

    private static String generateServerStorageName(@NotNull String uploadName) {
        String findName = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10);
        String extension = uploadName.substring(uploadName.lastIndexOf(".") + 1);
        return findName + "." + extension;
    }
}
