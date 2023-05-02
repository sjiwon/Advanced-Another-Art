package com.sjiwon.anotherart.auction.controller.dto.request;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BidRequest {
    @NotNull(message = "입찰가는 필수입니다.")
    @Positive(message = "입찰가는 음수가 될 수 없습니다.")
    private Integer bidAmount;
}
