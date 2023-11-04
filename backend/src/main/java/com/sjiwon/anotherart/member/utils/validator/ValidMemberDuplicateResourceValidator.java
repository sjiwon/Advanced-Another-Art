package com.sjiwon.anotherart.member.utils.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class ValidMemberDuplicateResourceValidator implements ConstraintValidator<ValidMemberDuplicateResource, String> {
    private static final List<String> ALLOWED_RESOURCES = List.of("nickname", "loginId", "phone", "email");

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        return ALLOWED_RESOURCES.contains(value);
    }
}
