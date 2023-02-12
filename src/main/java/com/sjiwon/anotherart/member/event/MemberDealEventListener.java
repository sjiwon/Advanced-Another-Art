package com.sjiwon.anotherart.member.event;

import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.point.PointDetail;
import com.sjiwon.anotherart.member.domain.point.PointDetailRepository;
import com.sjiwon.anotherart.member.domain.point.PointType;
import com.sjiwon.anotherart.member.service.MemberFindService;
import com.sjiwon.anotherart.purchase.event.AuctionArtDealEvent;
import com.sjiwon.anotherart.purchase.event.GeneralArtDealEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberDealEventListener {
    private final MemberFindService memberFindService;
    private final PointDetailRepository pointDetailRepository;

    @EventListener(GeneralArtDealEvent.class)
    public void generalArtDealEvent(GeneralArtDealEvent event) {
        Member owner = memberFindService.findById(event.getOwnerId());
        Member buyer = memberFindService.findById(event.getBuyerId());
        int dealAmount = event.getDealAmount();
        eventLogging(event.isAuctionDeal(), owner, buyer, dealAmount);

        // 판매자 포인트 추가
        owner.increasePoint(dealAmount);
        pointDetailRepository.save(PointDetail.insertPointDetail(owner, PointType.SOLD, dealAmount));

        // 구매자 포인트 차감
        buyer.decreasePoint(dealAmount);
        pointDetailRepository.save(PointDetail.insertPointDetail(buyer, PointType.PURCHASE, dealAmount));
    }

    @EventListener(AuctionArtDealEvent.class)
    public void auctionArtDealEvent(AuctionArtDealEvent event) {
        Member owner = memberFindService.findById(event.getOwnerId());
        Member buyer = memberFindService.findById(event.getBuyerId());
        int dealAmount = event.getDealAmount();
        eventLogging(event.isAuctionDeal(), owner, buyer, dealAmount);

        // 판매자 포인트 추가
        owner.increasePoint(dealAmount);
        pointDetailRepository.save(PointDetail.insertPointDetail(owner, PointType.SOLD, dealAmount));

        // 구매자 포인트 차감
        pointDetailRepository.save(PointDetail.insertPointDetail(buyer, PointType.PURCHASE, dealAmount));
    }

    private static void eventLogging(boolean isAuctionDeal, Member owner, Member buyer, int dealAmount) {
        log.info(
                "{} -> 판매자 [id={}, name={}] / 구매자 [id={}, name={}] / 가격 [{}포인트]",
                (isAuctionDeal) ? "경매 작품" : "일반 작품",
                owner.getId(),
                owner.getName(),
                buyer.getId(),
                buyer.getName(),
                dealAmount
        );
    }
}
