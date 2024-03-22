package com.sjiwon.anotherart.purchase.application.usecase;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.service.ArtReader;
import com.sjiwon.anotherart.global.annotation.UseCase;
import com.sjiwon.anotherart.global.lock.DistributedLock;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.service.MemberReader;
import com.sjiwon.anotherart.purchase.application.usecase.command.PurchaseArtCommand;
import com.sjiwon.anotherart.purchase.domain.service.PurchaseProcessor;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class PurchaseArtUseCase {
    private final ArtReader artReader;
    private final MemberReader memberReader;
    private final PurchaseProcessor purchaseProcessor;

    @DistributedLock(
            keyPrefix = "ART:",
            keySuffix = "#command.artId",
            withInTransaction = true
    )
    public void invoke(final PurchaseArtCommand command) {
        final Art art = artReader.getById(command.artId());
        final Member owner = memberReader.getById(art.getOwnerId());
        final Member buyer = memberReader.getById(command.memberId());

        if (art.isAuctionType()) {
            purchaseProcessor.purchaseAuctionArt(art, owner, buyer);
        } else {
            purchaseProcessor.purchaseGeneralArt(art, owner, buyer);
        }
    }
}
