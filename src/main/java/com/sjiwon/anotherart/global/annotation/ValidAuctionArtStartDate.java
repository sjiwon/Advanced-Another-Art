package com.sjiwon.anotherart.global.annotation;

import com.sjiwon.anotherart.art.exception.ArtRequestValidationMessage;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AuctionArtStartDateValidator.class)
public @interface ValidAuctionArtStartDate {
    String message() default ArtRequestValidationMessage.Registration.AUCTION_ART_START_DATE;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
