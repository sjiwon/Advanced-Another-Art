package com.sjiwon.anotherart.purchase.domain.service;

import com.sjiwon.anotherart.global.annotation.AnotherArtWritableTransactional;
import com.sjiwon.anotherart.member.domain.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssociatedPointTransactionProcessor {
    @AnotherArtWritableTransactional
    public void executeWithPurchaseAuction(
            final Member owner,
            final Member buyer,
            final int price
    ) {
        // 1. 구매자 포인트 차감
        buyer.increaseAvailablePoint(price); // 입찰 시 소모한 포인트 누적 차감 문제 해결
        buyer.decreaseTotalPoint(price);

        // 2. 판매자 포인트 적립
        owner.increaseTotalPoint(price);
    }

    @AnotherArtWritableTransactional
    public void executeWithPurchaseGeneral(
            final Member owner,
            final Member buyer,
            final int price
    ) {
        // 1. 구매자 포인트 차감
        buyer.decreaseTotalPoint(price);

        // 2. 판매자 포인트 적립
        owner.increaseTotalPoint(price);
    }
}
