package com.sjiwon.anotherart.member.controller.dto.request;

import com.sjiwon.anotherart.member.exception.MemberRequestValidationMessage;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PointChargeRequest {
    @NotNull(message = MemberRequestValidationMessage.PointCharge.CHARGE_AMOUNT)
    @Positive(message = MemberRequestValidationMessage.PointCharge.CHARGE_AMOUNT_POSITIVE)
    private Integer chargeAmount;
}
