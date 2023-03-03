package com.sjiwon.anotherart.member.controller.utils;

import com.sjiwon.anotherart.member.controller.dto.request.PointChargeRequest;

public class PointChargeRequestUtils {
    public static PointChargeRequest createRequest(int chargeAmount) {
        return PointChargeRequest.builder()
                .chargeAmount(chargeAmount)
                .build();
    }
}
