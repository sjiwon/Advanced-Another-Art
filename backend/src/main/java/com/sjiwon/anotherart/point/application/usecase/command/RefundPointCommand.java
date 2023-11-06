package com.sjiwon.anotherart.point.application.usecase.command;

public record RefundPointCommand(
        long memberId,
        int refundAmount
) {
}
