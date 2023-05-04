package com.sjiwon.anotherart.global.annotation.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ValidDuplicateResourceValidator implements ConstraintValidator<ValidDuplicateResource, String> {
    private static final List<String> ALLOWED_RESOURCES = List.of("nickname", "loginId", "phone", "email");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return ALLOWED_RESOURCES.contains(value);
    }
}
