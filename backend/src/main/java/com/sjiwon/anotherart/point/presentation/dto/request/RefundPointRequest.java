package com.sjiwon.anotherart.point.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RefundPointRequest(
        @NotNull(message = "포인트 환불 금액은 필수입니다.")
        @Positive(message = "포인트 환불 금액은 양수여야 합니다.")
        Integer refundAmount
) {
}
