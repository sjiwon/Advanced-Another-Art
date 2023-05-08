package com.sjiwon.anotherart.art.utils.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidImageContentTypeValidator.class)
public @interface ValidImageContentType {
    String message() default "작품 이미지가 업로드되지 않았습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
