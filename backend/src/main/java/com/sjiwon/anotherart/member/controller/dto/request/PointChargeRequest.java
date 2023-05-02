package com.sjiwon.anotherart.member.controller.dto.request;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PointChargeRequest {
    @NotNull(message = "포인트 충전 금액은 필수입니다.")
    @Positive(message = "포인트 충전 금액은 양수여야 합니다.")
    private Integer chargeAmount;
}
