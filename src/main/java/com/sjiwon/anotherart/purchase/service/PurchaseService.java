package com.sjiwon.anotherart.purchase.service;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.service.ArtFindService;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.auction.service.AuctionFindService;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.point.PointDetail;
import com.sjiwon.anotherart.member.domain.point.PointType;
import com.sjiwon.anotherart.member.service.MemberFindService;
import com.sjiwon.anotherart.purchase.domain.Purchase;
import com.sjiwon.anotherart.purchase.domain.PurchaseRepository;
import com.sjiwon.anotherart.purchase.exception.PurchaseErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PurchaseService {
    private final MemberFindService memberFindService;
    private final ArtFindService artFindService;
    private final AuctionFindService auctionFindService;
    private final PurchaseRepository purchaseRepository;

    @Transactional
    public void purchaseArt(Long artId, Long memberId) {
        Art art = artFindService.findById(artId);
        try {
            if (art.isAuctionType()) {
                auctionArtPurchaseProcess(art, memberId);
            } else {
                generalArtPurchaseProcess(art, memberId);
            }
        } catch (DataIntegrityViolationException e) {
            throw AnotherArtException.type(PurchaseErrorCode.ART_ALREADY_SOLD_OUT);
        }
    }

    private void auctionArtPurchaseProcess(Art art, Long memberId) {
        Auction auction = auctionFindService.findByArtId(art.getId());
        validateAuctionIsProceeding(auction);
        validateHighestBidder(auction, memberId);

        Member member = memberFindService.findById(memberId);
        purchaseRepository.save(Purchase.purchaseArt(member, art, auction.getCurrentHighestBidder().getBidAmount()));
        proceedingPointTransaction(auction, art, member);
    }

    private void validateAuctionIsProceeding(Auction auction) {
        if (!auction.isAuctionFinished()) {
            throw AnotherArtException.type(PurchaseErrorCode.AUCTION_NOT_FINISHED);
        }
    }

    private void validateHighestBidder(Auction auction, Long memberId) {
        Member bidder = auction.getCurrentHighestBidder().getBidder();
        if (!bidder.isSameMember(memberId)) {
            throw AnotherArtException.type(PurchaseErrorCode.INVALID_HIGHEST_BIDDER);
        }
    }

    private void generalArtPurchaseProcess(Art art, Long memberId) {
        Member member = memberFindService.findById(memberId);
        purchaseRepository.save(Purchase.purchaseArt(member, art, art.getPrice()));
        proceedingPointTransaction(null, art, member);
    }

    private void proceedingPointTransaction(@Nullable Auction auction, Art art, Member member) {
        Member owner = art.getOwner();
        if (auction == null) {
            owner.addPointDetail(PointDetail.insertPointDetail(owner, PointType.SOLD, art.getPrice()));
            member.addPointDetail(PointDetail.insertPointDetail(member, PointType.PURCHASE, art.getPrice()));
        } else {
            owner.addPointDetail(PointDetail.insertPointDetail(owner, PointType.SOLD, auction.getCurrentHighestBidder().getBidAmount()));
        }
    }
}
