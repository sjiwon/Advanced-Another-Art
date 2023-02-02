package com.sjiwon.anotherart.art.utils;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ArtImageValidator implements ConstraintValidator<ValidArtImage, MultipartFile> {
    private static final List<String> FILE_WHITELIST = List.of("image/png", "image/jpg", "image/jpeg");

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (!isSupportedContentType(file.getContentType())) {
            context.buildConstraintViolationWithTemplate("지원하지 않는 이미지 포맷입니다. [png / jpg / jpeg]");
            return false;
        }
        return !file.isEmpty();
    }

    private boolean isSupportedContentType(String contentType) {
        return FILE_WHITELIST.contains(contentType);
    }
}
