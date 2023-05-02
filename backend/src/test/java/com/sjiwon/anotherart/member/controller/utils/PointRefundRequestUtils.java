package com.sjiwon.anotherart.member.controller.utils;

import com.sjiwon.anotherart.member.controller.dto.request.PointRefundRequest;

public class PointRefundRequestUtils {
    public static PointRefundRequest createRequest(int refundAmount) {
        return PointRefundRequest.builder()
                .refundAmount(refundAmount)
                .build();
    }
}
