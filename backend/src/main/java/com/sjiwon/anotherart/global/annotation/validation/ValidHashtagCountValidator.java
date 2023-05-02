package com.sjiwon.anotherart.global.annotation.validation;

import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ValidHashtagCountValidator implements ConstraintValidator<ValidHashtagCount, List<String>> {
    private int minSize;
    private int maxSize;

    @Override
    public boolean isValid(List<String> hashtags, ConstraintValidatorContext context) {
        if (CollectionUtils.isEmpty(hashtags) || isHashtagSizeNotEnough(new HashSet<>(hashtags))) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(String.format("작품은 최소 %d개의 해시태그를 가져야 합니다.", minSize)).addConstraintViolation();
            return false;
        } else if (isHashtagSizeOverflow(new HashSet<>(hashtags))) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(String.format("작품은 최대 %d개의 해시태그를 가질 수 있습니다.", maxSize)).addConstraintViolation();
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
    public void initialize(ValidHashtagCount constraintAnnotation) {
        minSize = constraintAnnotation.min();
        maxSize = constraintAnnotation.max();
    }
}
