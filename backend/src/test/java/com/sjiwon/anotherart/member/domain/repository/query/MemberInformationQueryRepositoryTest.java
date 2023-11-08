package com.sjiwon.anotherart.member.domain.repository.query;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.model.ArtType;
import com.sjiwon.anotherart.art.domain.repository.ArtRepository;
import com.sjiwon.anotherart.auction.domain.model.Auction;
import com.sjiwon.anotherart.auction.domain.repository.AuctionRepository;
import com.sjiwon.anotherart.common.RepositoryTest;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import com.sjiwon.anotherart.member.domain.repository.query.dto.MemberInformation;
import com.sjiwon.anotherart.member.domain.repository.query.dto.MemberPointRecord;
import com.sjiwon.anotherart.member.domain.repository.query.dto.PurchaseArt;
import com.sjiwon.anotherart.member.domain.repository.query.dto.SoldArt;
import com.sjiwon.anotherart.member.domain.repository.query.dto.WinningAuctionArt;
import com.sjiwon.anotherart.point.domain.model.PointRecord;
import com.sjiwon.anotherart.point.domain.model.PointType;
import com.sjiwon.anotherart.point.domain.repository.PointRecordRepository;
import com.sjiwon.anotherart.purchase.domain.model.Purchase;
import com.sjiwon.anotherart.purchase.domain.repository.PurchaseRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;

import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_2;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_3;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.GENERAL_1;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.GENERAL_2;
import static com.sjiwon.anotherart.common.fixture.AuctionFixture.AUCTION_OPEN_NOW;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_B;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_C;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(MemberInformationQueryRepositoryImpl.class)
@DisplayName("Member -> MemberInformationQueryRepository 테스트")
class MemberInformationQueryRepositoryTest extends RepositoryTest {
    @Autowired
    private MemberInformationQueryRepositoryImpl sut;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PointRecordRepository pointRecordRepository;

    @Autowired
    private ArtRepository artRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @PersistenceContext
    private EntityManager em;

    private static final int MEMBER_INIT_POINT = 1_000_000;

    @Nested
    @DisplayName("사용자 기본 정보 조회 Query")
    class FetchInformation {
        @Test
        @DisplayName("사용자 기본 정보를 조회한다")
        void success() {
            // given
            final Member member = memberRepository.save(MEMBER_A.toMember());
            member.increaseTotalPoint(MEMBER_INIT_POINT);
            member.decreaseAvailablePoint(40_000);

            // when
            final MemberInformation result = sut.fetchInformation(member.getId());

            // then
            assertAll(
                    () -> assertThat(result.getId()).isEqualTo(member.getId()),
                    () -> assertThat(result.getName()).isEqualTo(member.getName()),
                    () -> assertThat(result.getNickname()).isEqualTo(member.getNickname().getValue()),
                    () -> assertThat(result.getLoginId()).isEqualTo(member.getLoginId()),
                    () -> assertThat(result.getSchool()).isEqualTo(member.getSchool()),
                    () -> assertThat(result.getPhone()).isEqualTo(member.getPhone().getValue()),
                    () -> assertThat(result.getEmail()).isEqualTo(member.getEmail().getValue()),
                    () -> assertThat(result.getAddress()).isEqualTo(member.getAddress()),
                    () -> assertThat(result.getTotalPoint()).isEqualTo(MEMBER_INIT_POINT),
                    () -> assertThat(result.getAvailablePoint()).isEqualTo(MEMBER_INIT_POINT - 40_000)
            );
        }
    }

    @Nested
    @DisplayName("포인트 활용 내역 조회 Query")
    class FetchPointRecords {
        private static final int INCREASE_AMOUNT = 100_000;
        private static final int DECREASE_AMOUNT = 50_000;

        @Test
        @DisplayName("포인트 활용 내역을 조회한다")
        void success() {
            // given
            final Member member = memberRepository.save(MEMBER_A.toMember());
            applyPointRecord(member, PointType.CHARGE, INCREASE_AMOUNT);
            applyPointRecord(member, PointType.CHARGE, INCREASE_AMOUNT);
            applyPointRecord(member, PointType.REFUND, DECREASE_AMOUNT);
            applyPointRecord(member, PointType.CHARGE, INCREASE_AMOUNT);
            applyPointRecord(member, PointType.PURCHASE, DECREASE_AMOUNT);
            applyPointRecord(member, PointType.PURCHASE, DECREASE_AMOUNT);
            applyPointRecord(member, PointType.CHARGE, INCREASE_AMOUNT);
            applyPointRecord(member, PointType.CHARGE, INCREASE_AMOUNT);
            applyPointRecord(member, PointType.PURCHASE, DECREASE_AMOUNT);
            applyPointRecord(member, PointType.SOLD, INCREASE_AMOUNT);
            applyPointRecord(member, PointType.SOLD, INCREASE_AMOUNT);

            // when
            final List<MemberPointRecord> result = sut.fetchPointRecords(member.getId());

            // then
            assertAll(
                    () -> assertThat(result).hasSize(11),
                    () -> assertThat(result)
                            .map(MemberPointRecord::getPointType)
                            .containsExactly(
                                    PointType.SOLD.getDescription(),
                                    PointType.SOLD.getDescription(),
                                    PointType.PURCHASE.getDescription(),
                                    PointType.CHARGE.getDescription(),
                                    PointType.CHARGE.getDescription(),
                                    PointType.PURCHASE.getDescription(),
                                    PointType.PURCHASE.getDescription(),
                                    PointType.CHARGE.getDescription(),
                                    PointType.REFUND.getDescription(),
                                    PointType.CHARGE.getDescription(),
                                    PointType.CHARGE.getDescription()
                            ),
                    () -> assertThat(result)
                            .map(MemberPointRecord::getAmount)
                            .containsExactly(
                                    INCREASE_AMOUNT,
                                    INCREASE_AMOUNT,
                                    DECREASE_AMOUNT,
                                    INCREASE_AMOUNT,
                                    INCREASE_AMOUNT,
                                    DECREASE_AMOUNT,
                                    DECREASE_AMOUNT,
                                    INCREASE_AMOUNT,
                                    DECREASE_AMOUNT,
                                    INCREASE_AMOUNT,
                                    INCREASE_AMOUNT
                            ),
                    () -> {
                        final MemberInformation memberInformation = sut.fetchInformation(member.getId());
                        assertThat(memberInformation.getTotalPoint()).isEqualTo(INCREASE_AMOUNT * 7 - DECREASE_AMOUNT * 4);
                        assertThat(memberInformation.getAvailablePoint()).isEqualTo(INCREASE_AMOUNT * 7 - DECREASE_AMOUNT * 4);
                    }
            );
        }

        private void applyPointRecord(final Member member, final PointType type, final int amount) {
            switch (type) {
                case CHARGE, SOLD -> {
                    member.increaseTotalPoint(amount);
                    pointRecordRepository.save(PointRecord.addPointRecord(member, type, amount));
                }
                default -> {
                    member.decreaseTotalPoint(amount);
                    pointRecordRepository.save(PointRecord.addPointRecord(member, type, amount));
                }
            }
        }
    }

    @Nested
    @DisplayName("낙찰된 경매 작품 조회 Query")
    class FetchWinningAuctionArts {
        private Member bidder;
        private Art artA;
        private Art artB;
        private Art artC;

        @BeforeEach
        void setUp() {
            bidder = memberRepository.save(MEMBER_A.toMember());
            bidder.increaseTotalPoint(MEMBER_INIT_POINT);

            final Member owner = memberRepository.save(MEMBER_B.toMember());
            artA = artRepository.save(AUCTION_1.toArt(owner));
            artB = artRepository.save(AUCTION_2.toArt(owner));
            artC = artRepository.save(AUCTION_3.toArt(owner));

            final Auction auctionA = auctionRepository.save(AUCTION_OPEN_NOW.toAuction(artA));
            final Auction auctionB = auctionRepository.save(AUCTION_OPEN_NOW.toAuction(artB));
            final Auction auctionC = auctionRepository.save(AUCTION_OPEN_NOW.toAuction(artC));
            auctionA.applyNewBid(bidder, artA.getPrice());
            auctionB.applyNewBid(bidder, artB.getPrice());
            auctionC.applyNewBid(bidder, artC.getPrice());
        }

        @Test
        @DisplayName("낙찰된 경매 작품을 조회한다")
        void success() {
            final List<WinningAuctionArt> result1 = sut.fetchWinningAuctionArts(bidder.getId());
            assertThat(result1).isEmpty();

            /* artA 경매 종료 */
            closeAuction(artA);
            final List<WinningAuctionArt> result2 = sut.fetchWinningAuctionArts(bidder.getId());
            assertThatWinningAuctionArtsMatch(result2, List.of(artA));

            /* artB 경매 종료 */
            closeAuction(artB);
            final List<WinningAuctionArt> result3 = sut.fetchWinningAuctionArts(bidder.getId());
            assertThatWinningAuctionArtsMatch(result3, List.of(artB, artA));

            /* artC 경매 종료 */
            closeAuction(artC);
            final List<WinningAuctionArt> result4 = sut.fetchWinningAuctionArts(bidder.getId());
            assertThatWinningAuctionArtsMatch(result4, List.of(artC, artB, artA));
        }

        private void closeAuction(final Art art) {
            em.createQuery("UPDATE Auction ac" +
                            " SET ac.period.startDate = :startDate, ac.period.endDate = :endDate" +
                            " WHERE ac.art.id = :artId")
                    .setParameter("startDate", LocalDateTime.now().minusDays(5))
                    .setParameter("endDate", LocalDateTime.now().minusDays(1))
                    .setParameter("artId", art.getId())
                    .executeUpdate();
        }

        private void assertThatWinningAuctionArtsMatch(
                final List<WinningAuctionArt> result,
                final List<Art> arts
        ) {
            assertThat(result).hasSize(arts.size());

            for (int i = 0; i < result.size(); i++) {
                final WinningAuctionArt winningAuctionArt = result.get(i);
                final Art art = arts.get(i);

                assertAll(
                        () -> assertThat(winningAuctionArt.getArtId()).isEqualTo(art.getId()),
                        () -> assertThat(winningAuctionArt.getHighestBidPrice()).isEqualTo(art.getPrice()),
                        () -> assertThat(winningAuctionArt.getArtHashtags()).containsExactlyInAnyOrderElementsOf(art.getHashtags())
                );
            }
        }
    }

    @Nested
    @DisplayName("판매한 작품 조회 Query")
    class FetchSoldArtsByType {
        private Member member;
        private Member buyerA;
        private Member buyerB;
        private Art generalArtA;
        private Art generalArtB;
        private Art auctionArtA;
        private Art auctionArtB;

        @BeforeEach
        void setUp() {
            member = memberRepository.save(MEMBER_A.toMember());
            member.increaseTotalPoint(MEMBER_INIT_POINT);
            buyerA = memberRepository.save(MEMBER_B.toMember());
            buyerA.increaseTotalPoint(MEMBER_INIT_POINT);
            buyerB = memberRepository.save(MEMBER_C.toMember());
            buyerB.increaseTotalPoint(MEMBER_INIT_POINT);

            generalArtA = artRepository.save(GENERAL_1.toArt(member));
            generalArtB = artRepository.save(GENERAL_2.toArt(member));
            auctionArtA = artRepository.save(AUCTION_1.toArt(member));
            auctionArtB = artRepository.save(AUCTION_2.toArt(member));
        }

        @Test
        @DisplayName("판매한 작품을 조회한다")
        void success() {
            final List<SoldArt> soldGeneralArts1 = sut.fetchSoldArtsByType(member.getId(), ArtType.GENERAL);
            final List<SoldArt> soldAuctionArts1 = sut.fetchSoldArtsByType(member.getId(), ArtType.AUCTION);
            assertThatSoldArtsMatch(
                    soldGeneralArts1,
                    List.of(),
                    List.of(),
                    soldAuctionArts1,
                    List.of(),
                    List.of()
            );

            /* generalArtA 판매 */
            sold(generalArtA, buyerA);

            final List<SoldArt> soldGeneralArts2 = sut.fetchSoldArtsByType(member.getId(), ArtType.GENERAL);
            final List<SoldArt> soldAuctionArts2 = sut.fetchSoldArtsByType(member.getId(), ArtType.AUCTION);
            assertThatSoldArtsMatch(
                    soldGeneralArts2,
                    List.of(generalArtA),
                    List.of(buyerA),
                    soldAuctionArts2,
                    List.of(),
                    List.of()
            );

            /* auctionArtB 판매 */
            sold(auctionArtB, buyerB);

            final List<SoldArt> soldGeneralArts3 = sut.fetchSoldArtsByType(member.getId(), ArtType.GENERAL);
            final List<SoldArt> soldAuctionArts3 = sut.fetchSoldArtsByType(member.getId(), ArtType.AUCTION);
            assertThatSoldArtsMatch(
                    soldGeneralArts3,
                    List.of(generalArtA),
                    List.of(buyerA),
                    soldAuctionArts3,
                    List.of(auctionArtB),
                    List.of(buyerB)
            );

            /* auctionArtA 판매 */
            sold(auctionArtA, buyerA);

            final List<SoldArt> soldGeneralArts4 = sut.fetchSoldArtsByType(member.getId(), ArtType.GENERAL);
            final List<SoldArt> soldAuctionArts4 = sut.fetchSoldArtsByType(member.getId(), ArtType.AUCTION);
            assertThatSoldArtsMatch(
                    soldGeneralArts4,
                    List.of(generalArtA),
                    List.of(buyerA),
                    soldAuctionArts4,
                    List.of(auctionArtA, auctionArtB),
                    List.of(buyerA, buyerB)
            );

            /* generalArtB 판매 */
            sold(generalArtB, buyerB);

            final List<SoldArt> soldGeneralArts5 = sut.fetchSoldArtsByType(member.getId(), ArtType.GENERAL);
            final List<SoldArt> soldAuctionArts5 = sut.fetchSoldArtsByType(member.getId(), ArtType.AUCTION);
            assertThatSoldArtsMatch(
                    soldGeneralArts5,
                    List.of(generalArtB, generalArtA),
                    List.of(buyerB, buyerA),
                    soldAuctionArts5,
                    List.of(auctionArtA, auctionArtB),
                    List.of(buyerA, buyerB)
            );
        }

        private void sold(final Art art, final Member buyer) {
            if (art.isAuctionType()) {
                purchaseRepository.save(Purchase.purchaseAuctionArt(art, buyer, art.getPrice()));
            } else {
                purchaseRepository.save(Purchase.purchaseGeneralArt(art, buyer));
            }
        }

        private void assertThatSoldArtsMatch(
                final List<SoldArt> soldGeneralArts,
                final List<Art> generalArts,
                final List<Member> generalArtBuyers,
                final List<SoldArt> soldAuctionArts,
                final List<Art> auctionArts,
                final List<Member> auctionArtBuyers
        ) {
            assertThat(soldGeneralArts).hasSize(generalArts.size());
            assertThat(soldAuctionArts).hasSize(auctionArts.size());

            for (int i = 0; i < soldGeneralArts.size(); i++) {
                final SoldArt soldArt = soldGeneralArts.get(i);
                final Art art = generalArts.get(i);
                final Member buyer = generalArtBuyers.get(i);

                assertAll(
                        () -> assertThat(soldArt.getArtId()).isEqualTo(art.getId()),
                        () -> assertThat(soldArt.getBuyerNickname()).isEqualTo(buyer.getNickname().getValue()),
                        () -> assertThat(soldArt.getSoldPrice()).isEqualTo(art.getPrice()),
                        () -> assertThat(soldArt.getArtHashtags()).containsExactlyInAnyOrderElementsOf(art.getHashtags())
                );
            }

            for (int i = 0; i < soldAuctionArts.size(); i++) {
                final SoldArt soldArt = soldAuctionArts.get(i);
                final Art art = auctionArts.get(i);
                final Member buyer = auctionArtBuyers.get(i);

                assertAll(
                        () -> assertThat(soldArt.getArtId()).isEqualTo(art.getId()),
                        () -> assertThat(soldArt.getBuyerNickname()).isEqualTo(buyer.getNickname().getValue()),
                        () -> assertThat(soldArt.getSoldPrice()).isEqualTo(art.getPrice()),
                        () -> assertThat(soldArt.getArtHashtags()).containsExactlyInAnyOrderElementsOf(art.getHashtags())
                );
            }
        }
    }

    @Nested
    @DisplayName("구매한 작품 조회 Query")
    class FetchPurchaseArtsByType {
        private Member member;
        private Art generalArtA;
        private Art generalArtB;
        private Art auctionArtA;
        private Art auctionArtB;

        @BeforeEach
        void setUp() {
            member = memberRepository.save(MEMBER_A.toMember());
            member.increaseTotalPoint(MEMBER_INIT_POINT);

            final Member owner = memberRepository.save(MEMBER_B.toMember());
            generalArtA = artRepository.save(GENERAL_1.toArt(owner));
            generalArtB = artRepository.save(GENERAL_2.toArt(owner));
            auctionArtA = artRepository.save(AUCTION_1.toArt(owner));
            auctionArtB = artRepository.save(AUCTION_2.toArt(owner));
        }

        @Test
        @DisplayName("구매한 작품을 조회한다")
        void success() {
            final List<PurchaseArt> purchaseGeneralArts1 = sut.fetchPurchaseArtsByType(member.getId(), ArtType.GENERAL);
            final List<PurchaseArt> purchaseAuctionArts1 = sut.fetchPurchaseArtsByType(member.getId(), ArtType.AUCTION);
            assertThatPurchaseArtsMatch(
                    purchaseGeneralArts1,
                    List.of(),
                    purchaseAuctionArts1,
                    List.of()
            );

            /* generalArtA 구매 */
            purchase(generalArtA);

            final List<PurchaseArt> purchaseGeneralArts2 = sut.fetchPurchaseArtsByType(member.getId(), ArtType.GENERAL);
            final List<PurchaseArt> purchaseAuctionArts2 = sut.fetchPurchaseArtsByType(member.getId(), ArtType.AUCTION);
            assertThatPurchaseArtsMatch(
                    purchaseGeneralArts2,
                    List.of(generalArtA),
                    purchaseAuctionArts2,
                    List.of()
            );

            /* auctionArtB 구매 */
            purchase(auctionArtB);

            final List<PurchaseArt> purchaseGeneralArts3 = sut.fetchPurchaseArtsByType(member.getId(), ArtType.GENERAL);
            final List<PurchaseArt> purchaseAuctionArts3 = sut.fetchPurchaseArtsByType(member.getId(), ArtType.AUCTION);
            assertThatPurchaseArtsMatch(
                    purchaseGeneralArts3,
                    List.of(generalArtA),
                    purchaseAuctionArts3,
                    List.of(auctionArtB)
            );

            /* auctionArtA 구매 */
            purchase(auctionArtA);

            final List<PurchaseArt> purchaseGeneralArts4 = sut.fetchPurchaseArtsByType(member.getId(), ArtType.GENERAL);
            final List<PurchaseArt> purchaseAuctionArts4 = sut.fetchPurchaseArtsByType(member.getId(), ArtType.AUCTION);
            assertThatPurchaseArtsMatch(
                    purchaseGeneralArts4,
                    List.of(generalArtA),
                    purchaseAuctionArts4,
                    List.of(auctionArtA, auctionArtB)
            );

            /* generalArtB 구매 */
            purchase(generalArtB);

            final List<PurchaseArt> purchaseGeneralArts5 = sut.fetchPurchaseArtsByType(member.getId(), ArtType.GENERAL);
            final List<PurchaseArt> purchaseAuctionArts5 = sut.fetchPurchaseArtsByType(member.getId(), ArtType.AUCTION);
            assertThatPurchaseArtsMatch(
                    purchaseGeneralArts5,
                    List.of(generalArtB, generalArtA),
                    purchaseAuctionArts5,
                    List.of(auctionArtA, auctionArtB)
            );
        }

        private void purchase(final Art art) {
            if (art.isAuctionType()) {
                purchaseRepository.save(Purchase.purchaseAuctionArt(art, member, art.getPrice()));
            } else {
                purchaseRepository.save(Purchase.purchaseGeneralArt(art, member));
            }
        }

        private void assertThatPurchaseArtsMatch(
                final List<PurchaseArt> purchaseGeneralArts,
                final List<Art> generalArts,
                final List<PurchaseArt> purchaseAuctionArts,
                final List<Art> auctionArts
        ) {
            assertThat(purchaseGeneralArts).hasSize(generalArts.size());
            assertThat(purchaseAuctionArts).hasSize(auctionArts.size());

            for (int i = 0; i < purchaseGeneralArts.size(); i++) {
                final PurchaseArt purchaseArt = purchaseGeneralArts.get(i);
                final Art art = generalArts.get(i);

                assertAll(
                        () -> assertThat(purchaseArt.getArtId()).isEqualTo(art.getId()),
                        () -> assertThat(purchaseArt.getPurchasePrice()).isEqualTo(art.getPrice()),
                        () -> assertThat(purchaseArt.getArtHashtags()).containsExactlyInAnyOrderElementsOf(art.getHashtags())
                );
            }

            for (int i = 0; i < purchaseAuctionArts.size(); i++) {
                final PurchaseArt purchaseArt = purchaseAuctionArts.get(i);
                final Art art = auctionArts.get(i);

                assertAll(
                        () -> assertThat(purchaseArt.getArtId()).isEqualTo(art.getId()),
                        () -> assertThat(purchaseArt.getPurchasePrice()).isEqualTo(art.getPrice()),
                        () -> assertThat(purchaseArt.getArtHashtags()).containsExactlyInAnyOrderElementsOf(art.getHashtags())
                );
            }
        }
    }
}
