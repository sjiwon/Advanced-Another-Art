package com.sjiwon.anotherart.global.annotation;

import com.sjiwon.anotherart.art.exception.ArtRequestValidationMessage;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ArtHashtagSizeValidator implements ConstraintValidator<ValidArtHashtagSize, List<String>> {
    private int minSize;
    private int maxSize;

    @Override
    public boolean isValid(List<String> hashtags, ConstraintValidatorContext context) {
        if (isHashtagSizeNotEnough(hashtags)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(String.format(ArtRequestValidationMessage.Registration.ART_HASHTAG_LIST_MIN, minSize)).addConstraintViolation();
            return false;
        } else if (isHashtagSizeOverflow(hashtags)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(String.format(ArtRequestValidationMessage.Registration.ART_HASHTAG_LIST_MAX, maxSize)).addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean isHashtagSizeNotEnough(List<String> hashtags) {
        return hashtags.size() <= minSize;
    }

    private boolean isHashtagSizeOverflow(List<String> hashtags) {
        return maxSize <= hashtags.size();
    }

    @Override
    public void initialize(ValidArtHashtagSize constraintAnnotation) {
        minSize = constraintAnnotation.min();
        maxSize = constraintAnnotation.max();
    }
}
