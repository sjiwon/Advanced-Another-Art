package com.sjiwon.anotherart.member.controller.dto.request;

import com.sjiwon.anotherart.member.exception.MemberRequestValidationMessage;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PointRefundRequest {
    @NotNull(message = MemberRequestValidationMessage.PointRefund.REFUND_AMOUNT)
    @Positive(message = MemberRequestValidationMessage.PointRefund.REFUND_AMOUNT_POSITIVE)
    private Integer refundAmount;
}
