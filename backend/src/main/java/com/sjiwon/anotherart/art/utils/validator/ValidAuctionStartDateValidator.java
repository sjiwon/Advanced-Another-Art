package com.sjiwon.anotherart.art.utils.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class ValidAuctionStartDateValidator implements ConstraintValidator<ValidAuctionStartDate, LocalDateTime> {
    @Override
    public boolean isValid(LocalDateTime startDate, ConstraintValidatorContext context) {
        if (startDate == null) { // 일반 작품 -> Validation X
            return true;
        }
        return startDate.isAfter(LocalDateTime.now());
    }
}
