package com.sjiwon.anotherart.art.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class UploadImage {
    @Column(name = "upload_file_name", nullable = false)
    private String uploadFileName;

    @Column(name = "link", nullable = false)
    private String link;

    public UploadImage(final String uploadFileName, final String link) {
        this.uploadFileName = uploadFileName;
        this.link = link;
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        final UploadImage other = (UploadImage) object;

        if (!uploadFileName.equals(other.uploadFileName)) return false;
        return link.equals(other.link);
    }

    @Override
    public int hashCode() {
        int result = uploadFileName.hashCode();
        result = 31 * result + link.hashCode();
        return result;
    }
}
