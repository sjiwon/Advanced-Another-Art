package com.sjiwon.anotherart.purchase.application.usecase.command;

public record PurchaseArtCommand(
        long memberId,
        long artId
) {
}
