package com.sjiwon.anotherart.global.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class AuctionArtStartDateValidator implements ConstraintValidator<ValidAuctionArtStartDate, LocalDateTime> {
    @Override
    public boolean isValid(LocalDateTime startDate, ConstraintValidatorContext context) {
        if (startDate == null) { // μΌλ° μν -> Validation X
            return true;
        }
        return startDate.isAfter(LocalDateTime.now());
    }
}
