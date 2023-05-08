package com.sjiwon.anotherart.member.utils.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ValidMemberDuplicateResourceValidator implements ConstraintValidator<ValidMemberDuplicateResource, String> {
    private static final List<String> ALLOWED_RESOURCES = List.of("nickname", "loginId", "phone", "email");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return ALLOWED_RESOURCES.contains(value);
    }
}
