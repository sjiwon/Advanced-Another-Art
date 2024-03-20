package com.sjiwon.anotherart.point.presentation.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ChargePointRequest(
        @NotNull(message = "포인트 충전 금액은 필수입니다.")
        @Positive(message = "포인트 충전 금액은 양수여야 합니다.")
        Integer chargeAmount
) {
}
