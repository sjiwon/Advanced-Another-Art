package com.sjiwon.anotherart.global.annotation;

import com.sjiwon.anotherart.token.domain.model.TokenType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExtractToken {
    TokenType tokenType() default TokenType.ACCESS;
}
