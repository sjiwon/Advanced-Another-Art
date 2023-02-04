package com.sjiwon.anotherart.global.annotation;

import com.sjiwon.anotherart.art.exception.ArtRequestValidationMessage;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArtHashtagSizeValidator implements ConstraintValidator<ValidArtHashtagSize, List<String>> {
    private int minSize;
    private int maxSize;

    @Override
    public boolean isValid(List<String> hashtags, ConstraintValidatorContext context) {
        if (CollectionUtils.isEmpty(hashtags) || isHashtagSizeNotEnough(new HashSet<>(hashtags))) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(String.format(ArtRequestValidationMessage.ART_HASHTAG_LIST_MIN, minSize)).addConstraintViolation();
            return false;
        } else if (isHashtagSizeOverflow(new HashSet<>(hashtags))) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(String.format(ArtRequestValidationMessage.ART_HASHTAG_LIST_MAX, maxSize)).addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean isHashtagSizeNotEnough(Set<String> hashtags) {
        return hashtags.size() < minSize;
    }

    private boolean isHashtagSizeOverflow(Set<String> hashtags) {
        return maxSize < hashtags.size();
    }

    @Override
    public void initialize(ValidArtHashtagSize constraintAnnotation) {
        minSize = constraintAnnotation.min();
        maxSize = constraintAnnotation.max();
    }
}
