package com.sjiwon.anotherart.art.utils.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class ValidArtTypeValidator implements ConstraintValidator<ValidArtType, String> {
    private static final List<String> ALLOWED_TYPE = List.of("general", "auction");

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        return ALLOWED_TYPE.contains(value);
    }
}
