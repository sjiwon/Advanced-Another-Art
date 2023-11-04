package com.sjiwon.anotherart.art.utils.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class ValidArtDuplicateResourceValidator implements ConstraintValidator<ValidArtDuplicateResource, String> {
    private static final List<String> ALLOWED_RESOURCES = List.of("name");

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        return ALLOWED_RESOURCES.contains(value);
    }
}
