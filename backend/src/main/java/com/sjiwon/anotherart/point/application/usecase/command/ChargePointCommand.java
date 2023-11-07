package com.sjiwon.anotherart.point.application.usecase.command;

public record ChargePointCommand(
        long memberId,
        int chargeAmount
) {
}
