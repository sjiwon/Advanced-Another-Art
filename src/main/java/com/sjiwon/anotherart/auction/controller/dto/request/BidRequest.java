package com.sjiwon.anotherart.auction.controller.dto.request;

import com.sjiwon.anotherart.auction.exception.BidRequestValidationMessage;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BidRequest {
    @NotNull(message = BidRequestValidationMessage.Bid.BID_AMOUNT)
    @Positive(message = BidRequestValidationMessage.Bid.BID_AMOUNT_POSITIVE)
    private Integer bidAmount;
}
