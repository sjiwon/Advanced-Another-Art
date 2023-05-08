package com.sjiwon.anotherart.art.utils.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ValidArtTypeValidator implements ConstraintValidator<ValidArtType, String> {
    private static final List<String> ALLOWED_TYPE = List.of("general", "auction");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return ALLOWED_TYPE.contains(value);
    }
}
