package com.sjiwon.anotherart.art.utils.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class ValidHashtagCountValidator implements ConstraintValidator<ValidHashtagCount, Set<String>> {
    private static final String ART_HASHTAG_LIST_MIN = "작품은 최소 %d개의 해시태그를 가져야 합니다.";
    private static final String ART_HASHTAG_LIST_MAX = "작품은 최대 %d개의 해시태그를 가질 수 있습니다.";

    private int min;
    private int max;

    @Override
    public void initialize(final ValidHashtagCount constraintAnnotation) {
        min = constraintAnnotation.min();
        max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(final Set<String> hashtags, final ConstraintValidatorContext context) {
        if (hashtags.size() < min) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(String.format(ART_HASHTAG_LIST_MIN, min))
                    .addConstraintViolation();
            return false;
        }

        if (hashtags.size() > max) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(String.format(ART_HASHTAG_LIST_MAX, max))
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
