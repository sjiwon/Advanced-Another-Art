package com.sjiwon.anotherart.art.utils.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ValidArtDuplicateResourceValidator implements ConstraintValidator<ValidArtDuplicateResource, String> {
    private static final List<String> ALLOWED_RESOURCES = List.of("name");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return ALLOWED_RESOURCES.contains(value);
    }
}
