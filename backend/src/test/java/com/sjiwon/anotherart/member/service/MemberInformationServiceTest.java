package com.sjiwon.anotherart.member.service;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.infra.query.dto.response.AuctionArt;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.common.ServiceTest;
import com.sjiwon.anotherart.favorite.domain.Favorite;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.infra.query.dto.response.MemberPointRecord;
import com.sjiwon.anotherart.member.infra.query.dto.response.TradedArt;
import com.sjiwon.anotherart.member.service.dto.response.MemberInformation;
import com.sjiwon.anotherart.member.service.dto.response.PointRecordAssembler;
import com.sjiwon.anotherart.member.service.dto.response.TradedArtAssembler;
import com.sjiwon.anotherart.member.service.dto.response.WinningAuctionArtAssembler;
import com.sjiwon.anotherart.purchase.domain.Purchase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.sjiwon.anotherart.art.domain.ArtType.AUCTION;
import static com.sjiwon.anotherart.art.domain.ArtType.GENERAL;
import static com.sjiwon.anotherart.fixture.AuctionFixture.AUCTION_OPEN_NOW;
import static com.sjiwon.anotherart.fixture.MemberFixture.*;
import static com.sjiwon.anotherart.member.domain.point.PointType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member [Service Layer] -> MemberInformationService 테스트")
class MemberInformationServiceTest extends ServiceTest {
    @Autowired
    private MemberInformationService memberInformationService;

    private Member member;
    private Member owner;
    private Member buyer;
    private final Member[] bidders = new Member[10];
    private final Art[] generalArts = new Art[10];
    private final Art[] auctionArts = new Art[10];
    private final Auction[] auctions = new Auction[10];

    @BeforeEach
    void setUp() {
        member = createMember(MEMBER_A);
        owner = createMember(MEMBER_B);
        buyer = createMember(MEMBER_C);

        initDummyBidders();
        initArtsAndAuctions();
    }

    private Member createMember(MemberFixture fixture) {
        Member member = fixture.toMember();
        member.addPointRecords(CHARGE, 100_000_000);
        return memberRepository.save(member);
    }

    private void initDummyBidders() {
        List<MemberFixture> dummyFixtures = Arrays.stream(MemberFixture.values())
                .filter(member -> member.getLoginId().startsWith("dummy"))
                .toList();
        Arrays.setAll(bidders, i -> createMember(dummyFixtures.get(i)));
    }

    private void initArtsAndAuctions() {
        List<ArtFixture> generalArtFixtures = Arrays.stream(ArtFixture.values())
                .filter(art -> art.getType() == GENERAL)
                .toList();
        List<ArtFixture> auctionArtFixtures = Arrays.stream(ArtFixture.values())
                .filter(art -> art.getType() == AUCTION)
                .toList();
        Arrays.setAll(generalArts, i -> artRepository.save(generalArtFixtures.get(i).toArt(owner)));
        Arrays.setAll(auctionArts, i -> artRepository.save(auctionArtFixtures.get(i).toArt(owner)));
        Arrays.setAll(auctions, i -> auctionRepository.save(AUCTION_OPEN_NOW.toAuction(auctionArts[i])));
    }

    @Test
    @DisplayName("사용자의 기본 정보를 조회한다")
    void getInformation() {
        // when
        MemberInformation information = memberInformationService.getInformation(member.getId());

        // then
        assertAll(
                () -> assertThat(information.id()).isEqualTo(member.getId()),
                () -> assertThat(information.name()).isEqualTo(member.getName()),
                () -> assertThat(information.nickname()).isEqualTo(member.getNicknameValue()),
                () -> assertThat(information.loginId()).isEqualTo(member.getLoginId()),
                () -> assertThat(information.school()).isEqualTo(member.getSchool()),
                () -> assertThat(information.phone()).isEqualTo(member.getPhone()),
                () -> assertThat(information.email()).isEqualTo(member.getEmailValue()),
                () -> assertThat(information.address().getPostcode()).isEqualTo(member.getAddress().getPostcode()),
                () -> assertThat(information.address().getDefaultAddress()).isEqualTo(member.getAddress().getDefaultAddress()),
                () -> assertThat(information.address().getDetailAddress()).isEqualTo(member.getAddress().getDetailAddress()),
                () -> assertThat(information.totalPoint()).isEqualTo(member.getTotalPoint()),
                () -> assertThat(information.availablePoint()).isEqualTo(member.getAvailablePoint())
        );
    }

    @Test
    @DisplayName("사용자의 포인트 활용 내역을 조회한다")
    void getPointRecords() {
        // given
        member.addPointRecords(CHARGE, 100_000);
        member.addPointRecords(CHARGE, 200_000);
        member.addPointRecords(PURCHASE, 30_000);
        member.addPointRecords(PURCHASE, 50_000);
        member.addPointRecords(REFUND, 50_000);
        member.addPointRecords(SOLD, 100_000);
        member.addPointRecords(CHARGE, 500_000);

        // when
        PointRecordAssembler pointRecords = memberInformationService.getPointRecords(member.getId());
        List<MemberPointRecord> result = pointRecords.result();

        // then
        assertAll(
                () -> assertThat(result).hasSize(8),
                () -> assertThat(result)
                        .map(MemberPointRecord::getPointType)
                        .containsExactly(
                                CHARGE.getDescription(),
                                SOLD.getDescription(),
                                REFUND.getDescription(),
                                PURCHASE.getDescription(),
                                PURCHASE.getDescription(),
                                CHARGE.getDescription(),
                                CHARGE.getDescription(),
                                CHARGE.getDescription()
                        ),
                () -> assertThat(result)
                        .map(MemberPointRecord::getAmount)
                        .containsExactly(
                                500_000,
                                100_000,
                                50_000,
                                50_000,
                                30_000,
                                200_000,
                                100_000,
                                100_000_000
                        )
        );
    }

    @Test
    @DisplayName("낙찰된 경매 작품을 조회한다")
    void getWinningAuctionArts() {
        /* 7건 입찰 & 5건 낙찰 */
        bid(
                List.of(10, 4, 10, 6, 10, 10, 10),
                List.of(auctions[0], auctions[2], auctions[3], auctions[5], auctions[6], auctions[8], auctions[9])
        );
        makeAuctionEnd(auctions[0], auctions[2], auctions[3], auctions[5], auctions[6], auctions[8], auctions[9]);

        WinningAuctionArtAssembler assembler1 = memberInformationService.getWinningAuctionArts(bidders[0].getId());
        assertThatWinningAuctionMatch(
                assembler1.result(),
                List.of(9, 8, 6, 3, 0)
        );

        /* 추가 3건 입찰 & 2건 낙찰 */
        bid(
                List.of(10, 3, 10),
                List.of(auctions[1], auctions[4], auctions[7])
        );
        makeAuctionEnd(auctions[1], auctions[4], auctions[7]);

        WinningAuctionArtAssembler assembler2 = memberInformationService.getWinningAuctionArts(bidders[0].getId());
        assertThatWinningAuctionMatch(
                assembler2.result(),
                List.of(9, 8, 7, 6, 3, 1, 0)
        );
    }

    @Test
    @DisplayName("판매한 작품을 조회한다")
    void getSoldArts() {
        /* 경매 작품 3건, 일반 작품 5건 */
        purchaseAuctionArt(auctionArts[0], auctionArts[5], auctionArts[8]);
        purchaseGeneralArt(generalArts[0], generalArts[1], generalArts[3], generalArts[6], generalArts[9]);

        TradedArtAssembler assembler1 = memberInformationService.getSoldArts(owner.getId());
        assertThatTradedArtMatch(
                assembler1.tradedAuctions(),
                List.of(8, 5, 0),
                assembler1.tradedGenerals(),
                List.of(9, 6, 3, 1, 0)
        );

        /* 경매 작품 추가 4건, 일반 작품 추가 1건 */
        purchaseAuctionArt(auctionArts[1], auctionArts[2], auctionArts[4], auctionArts[9]);
        purchaseGeneralArt(generalArts[5]);

        TradedArtAssembler assembler2 = memberInformationService.getSoldArts(owner.getId());
        assertThatTradedArtMatch(
                assembler2.tradedAuctions(),
                List.of(9, 8, 5, 4, 2, 1, 0),
                assembler2.tradedGenerals(),
                List.of(9, 6, 5, 3, 1, 0)
        );
    }

    @Test
    @DisplayName("구매한 작품을 조회한다")
    void getPurchaseArts() {
        /* 경매 작품 3건, 일반 작품 5건 */
        purchaseAuctionArt(auctionArts[0], auctionArts[5], auctionArts[8]);
        purchaseGeneralArt(generalArts[0], generalArts[1], generalArts[3], generalArts[6], generalArts[9]);

        TradedArtAssembler assembler1 = memberInformationService.getPurchaseArts(buyer.getId());
        assertThatTradedArtMatch(
                assembler1.tradedAuctions(),
                List.of(8, 5, 0),
                assembler1.tradedGenerals(),
                List.of(9, 6, 3, 1, 0)
        );

        /* 경매 작품 추가 4건, 일반 작품 추가 1건 */
        purchaseAuctionArt(auctionArts[1], auctionArts[2], auctionArts[4], auctionArts[9]);
        purchaseGeneralArt(generalArts[5]);

        TradedArtAssembler assembler2 = memberInformationService.getPurchaseArts(buyer.getId());
        assertThatTradedArtMatch(
                assembler2.tradedAuctions(),
                List.of(9, 8, 5, 4, 2, 1, 0),
                assembler2.tradedGenerals(),
                List.of(9, 6, 5, 3, 1, 0)
        );
    }

    private void bid(List<Integer> bidCounts, List<Auction> auctions) {
        for (int i = 0; i < bidCounts.size(); i++) {
            int bidCount = bidCounts.get(i);
            Auction auction = auctions.get(i);

            for (int index = bidders.length - 1; index >= bidders.length - bidCount; index--) {
                Member bidder = bidders[index];
                auction.applyNewBid(bidder, auction.getHighestBidPrice() + 50_000);
            }
            favoriteRepository.save(Favorite.favoriteMarking(auction.getArt().getId(), 1L));
        }
    }

    private void makeAuctionEnd(Auction... auctions) {
        List<Long> auctionIds = Arrays.stream(auctions)
                .map(Auction::getId)
                .toList();

        em.createQuery("UPDATE Auction a" +
                        " SET a.period.endDate = :endDate" +
                        " WHERE a.id IN :auctionIds")
                .setParameter("endDate", LocalDateTime.now().minusDays(1))
                .setParameter("auctionIds", auctionIds)
                .executeUpdate();
    }

    private void purchaseAuctionArt(Art... arts) {
        for (Art art : arts) {
            purchaseRepository.save(Purchase.purchaseAuctionArt(art, buyer, art.getPrice()));
            favoriteRepository.save(Favorite.favoriteMarking(art.getId(), 1L));
        }
    }

    private void purchaseGeneralArt(Art... arts) {
        for (Art art : arts) {
            purchaseRepository.save(Purchase.purchaseGeneralArt(art, buyer));
            favoriteRepository.save(Favorite.favoriteMarking(art.getId(), 1L));
        }
    }

    private void assertThatWinningAuctionMatch(List<AuctionArt> result, List<Integer> indices) {
        int totalSize = indices.size();
        assertThat(result).hasSize(totalSize);

        for (int i = 0; i < totalSize; i++) {
            AuctionArt auctionArt = result.get(i);
            int index = indices.get(i);

            Auction auction = auctions[index];
            Art art = auctionArts[index];

            assertAll(
                    () -> assertThat(auctionArt.getAuction().getId()).isEqualTo(auction.getId()),
                    () -> assertThat(auctionArt.getAuction().getHighestBidPrice()).isEqualTo(auction.getHighestBidPrice()),
                    () -> assertThat(auctionArt.getAuction().getBidCount()).isEqualTo(10),

                    () -> assertThat(auctionArt.getArt().getId()).isEqualTo(art.getId()),
                    () -> assertThat(auctionArt.getArt().getName()).isEqualTo(art.getNameValue()),
                    () -> assertThat(auctionArt.getArt().getDescription()).isEqualTo(art.getDescriptionValue()),
                    () -> assertThat(auctionArt.getArt().getPrice()).isEqualTo(art.getPrice()),
                    () -> assertThat(auctionArt.getArt().getStatus()).isEqualTo(art.getStatus().getDescription()),
                    () -> assertThat(auctionArt.getArt().getStorageName()).isEqualTo(art.getStorageName()),
                    () -> assertThat(auctionArt.getArt().getHashtags()).containsExactlyInAnyOrderElementsOf(art.getHashtags()),
                    () -> assertThat(auctionArt.getArt().getLikeMembers()).hasSize(1),

                    () -> assertThat(auctionArt.getOwner().id()).isEqualTo(owner.getId()),
                    () -> assertThat(auctionArt.getOwner().nickname()).isEqualTo(owner.getNicknameValue()),
                    () -> assertThat(auctionArt.getOwner().school()).isEqualTo(owner.getSchool()),

                    () -> assertThat(auctionArt.getHighestBidder().id()).isEqualTo(bidders[0].getId()),
                    () -> assertThat(auctionArt.getHighestBidder().nickname()).isEqualTo(bidders[0].getNicknameValue()),
                    () -> assertThat(auctionArt.getHighestBidder().school()).isEqualTo(bidders[0].getSchool())
            );
        }
    }

    private void assertThatTradedArtMatch(List<TradedArt> auctionResult, List<Integer> auctionIndices,
                                          List<TradedArt> generalResult, List<Integer> generalIndices) {
        int auctionTotalSize = auctionIndices.size();
        int generalTotalSize = generalIndices.size();
        assertThat(auctionResult).hasSize(auctionTotalSize);
        assertThat(generalResult).hasSize(generalTotalSize);

        for (int i = 0; i < auctionTotalSize; i++) {
            TradedArt tradedArt = auctionResult.get(i);
            int index = auctionIndices.get(i);

            Art art = auctionArts[index];

            assertAll(
                    () -> assertThat(tradedArt.getArt().getId()).isEqualTo(art.getId()),
                    () -> assertThat(tradedArt.getArt().getName()).isEqualTo(art.getNameValue()),
                    () -> assertThat(tradedArt.getArt().getDescription()).isEqualTo(art.getDescriptionValue()),
                    () -> assertThat(tradedArt.getArt().getPrice()).isEqualTo(art.getPrice()),
                    () -> assertThat(tradedArt.getArt().getStatus()).isEqualTo(art.getStatus().getDescription()),
                    () -> assertThat(tradedArt.getArt().getStorageName()).isEqualTo(art.getStorageName()),
                    () -> assertThat(tradedArt.getArt().getHashtags()).containsExactlyInAnyOrderElementsOf(art.getHashtags()),
                    () -> assertThat(tradedArt.getArt().getLikeMembers()).hasSize(1),

                    () -> assertThat(tradedArt.getOwner().id()).isEqualTo(owner.getId()),
                    () -> assertThat(tradedArt.getOwner().nickname()).isEqualTo(owner.getNicknameValue()),
                    () -> assertThat(tradedArt.getOwner().school()).isEqualTo(owner.getSchool()),

                    () -> assertThat(tradedArt.getBuyer().id()).isEqualTo(buyer.getId()),
                    () -> assertThat(tradedArt.getBuyer().nickname()).isEqualTo(buyer.getNicknameValue()),
                    () -> assertThat(tradedArt.getBuyer().school()).isEqualTo(buyer.getSchool())
            );
        }

        for (int i = 0; i < generalTotalSize; i++) {
            TradedArt tradedArt = generalResult.get(i);
            int index = generalIndices.get(i);

            Art art = generalArts[index];

            assertAll(
                    () -> assertThat(tradedArt.getArt().getId()).isEqualTo(art.getId()),
                    () -> assertThat(tradedArt.getArt().getName()).isEqualTo(art.getNameValue()),
                    () -> assertThat(tradedArt.getArt().getDescription()).isEqualTo(art.getDescriptionValue()),
                    () -> assertThat(tradedArt.getArt().getPrice()).isEqualTo(art.getPrice()),
                    () -> assertThat(tradedArt.getArt().getStatus()).isEqualTo(art.getStatus().getDescription()),
                    () -> assertThat(tradedArt.getArt().getStorageName()).isEqualTo(art.getStorageName()),
                    () -> assertThat(tradedArt.getArt().getHashtags()).containsExactlyInAnyOrderElementsOf(art.getHashtags()),
                    () -> assertThat(tradedArt.getArt().getLikeMembers()).hasSize(1),

                    () -> assertThat(tradedArt.getOwner().id()).isEqualTo(owner.getId()),
                    () -> assertThat(tradedArt.getOwner().nickname()).isEqualTo(owner.getNicknameValue()),
                    () -> assertThat(tradedArt.getOwner().school()).isEqualTo(owner.getSchool()),

                    () -> assertThat(tradedArt.getBuyer().id()).isEqualTo(buyer.getId()),
                    () -> assertThat(tradedArt.getBuyer().nickname()).isEqualTo(buyer.getNicknameValue()),
                    () -> assertThat(tradedArt.getBuyer().school()).isEqualTo(buyer.getSchool())
            );
        }
    }
}
