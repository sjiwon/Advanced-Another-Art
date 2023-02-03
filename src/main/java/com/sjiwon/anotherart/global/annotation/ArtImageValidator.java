package com.sjiwon.anotherart.global.annotation;

import com.sjiwon.anotherart.art.exception.ArtRequestValidationMessage;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ArtImageValidator implements ConstraintValidator<ValidArtImage, MultipartFile> {
    private static final List<String> FILE_WHITELIST = List.of("image/png", "image/jpg", "image/jpeg");

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (ObjectUtils.isEmpty(file) || file.isEmpty()) {
            return false;
        } else if (!isSupportedContentType(file.getContentType())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ArtRequestValidationMessage.Registration.ART_FILE_FORMAT).addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean isSupportedContentType(String contentType) {
        return FILE_WHITELIST.contains(contentType);
    }
}
