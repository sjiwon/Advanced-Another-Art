package com.sjiwon.anotherart.art.utils.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidArtTypeValidator.class)
public @interface ValidArtType {
    String message() default "제공하지 않는 작품 타입입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
