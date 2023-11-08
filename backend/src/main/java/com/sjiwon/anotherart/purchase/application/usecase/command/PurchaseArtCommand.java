package com.sjiwon.anotherart.purchase.application.usecase.command;

public record PurchaseArtCommand(
        Long memberId,
        Long artId
) {
}
