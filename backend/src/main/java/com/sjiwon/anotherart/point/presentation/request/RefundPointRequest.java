package com.sjiwon.anotherart.point.presentation.request;

import com.sjiwon.anotherart.point.application.usecase.command.RefundPointCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RefundPointRequest(
        @NotNull(message = "포인트 환불 금액은 필수입니다.")
        @Positive(message = "포인트 환불 금액은 양수여야 합니다.")
        Integer refundAmount
) {
    public RefundPointCommand toCommand(final long memberId) {
        return new RefundPointCommand(memberId, refundAmount);
    }
}
