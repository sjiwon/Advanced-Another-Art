package com.sjiwon.anotherart.art.utils.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class ValidImageContentTypeValidator implements ConstraintValidator<ValidImageContentType, MultipartFile> {
    private static final List<String> ALLOWED_CONTENT_TYPE = List.of("image/png", "image/jpg", "image/jpeg");

    @Override
    public boolean isValid(final MultipartFile file, final ConstraintValidatorContext context) {
        if (ObjectUtils.isEmpty(file) || file.isEmpty()) {
            return false;
        }

        if (!isSupportedContentType(file.getContentType())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("이미지는 jpg, jpeg, png만 허용합니다.")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean isSupportedContentType(final String contentType) {
        return ALLOWED_CONTENT_TYPE.contains(contentType);
    }
}
