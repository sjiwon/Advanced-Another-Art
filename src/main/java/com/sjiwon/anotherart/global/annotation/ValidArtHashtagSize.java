package com.sjiwon.anotherart.global.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ArtHashtagSizeValidator.class)
public @interface ValidArtHashtagSize {
    String message() default "";
    int max() default 10;
    int min() default 1;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
