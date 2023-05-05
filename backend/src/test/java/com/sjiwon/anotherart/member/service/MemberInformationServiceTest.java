package com.sjiwon.anotherart.member.service;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.infra.query.dto.response.AuctionArt;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.common.ServiceTest;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.infra.query.dto.response.MemberPointRecord;
import com.sjiwon.anotherart.member.service.dto.response.MemberInformation;
import com.sjiwon.anotherart.member.service.dto.response.PointRecordAssembler;
import com.sjiwon.anotherart.member.service.dto.response.WinningAuctionArtAssembler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.sjiwon.anotherart.art.domain.ArtType.AUCTION;
import static com.sjiwon.anotherart.fixture.AuctionFixture.AUCTION_OPEN_NOW;
import static com.sjiwon.anotherart.fixture.MemberFixture.*;
import static com.sjiwon.anotherart.member.domain.point.PointType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member [Service Layer] -> MemberPointService 테스트")
class MemberInformationServiceTest extends ServiceTest {
    @Autowired
    private MemberInformationService memberInformationService;

    private Member member;
    private Member owner;
    private Member bidder;
    private final Art[] arts = new Art[10];
    private final Auction[] auctions = new Auction[10];

    @BeforeEach
    void setUp() {
        member = createMember(MEMBER_A);
        owner = createMember(MEMBER_B);
        bidder = createMember(MEMBER_C);
        initArtsAndAuctions();
    }

    private Member createMember(MemberFixture fixture) {
        Member member = fixture.toMember();
        member.addPointRecords(CHARGE, 1_000_000);
        return memberRepository.save(member);
    }

    private void initArtsAndAuctions() {
        List<ArtFixture> auctionArts = Arrays.stream(ArtFixture.values())
                .filter(art -> art.getType() == AUCTION)
                .toList();
        Arrays.setAll(arts, i -> artRepository.save(auctionArts.get(i).toArt(owner)));
        Arrays.setAll(auctions, i -> auctionRepository.save(AUCTION_OPEN_NOW.toAuction(arts[i])));
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
                                1_000_000
                        )
        );
    }

    @Test
    @DisplayName("특정 사용자에 대해서 낙찰된 경매 작품을 조회한다")
    void getWinningAuctionArts() {
        /* 7건 입찰 & 낙찰 */
        bid(auctions[0], auctions[2], auctions[3], auctions[5], auctions[6], auctions[8], auctions[9]);
        makeAuctionEnd(auctions[0], auctions[2], auctions[3], auctions[5], auctions[6], auctions[8], auctions[9]);

        WinningAuctionArtAssembler assembler1 = memberInformationService.getWinningAuctionArts(bidder.getId());
        assertThatWinningAuctionMatch(
                assembler1.result(),
                List.of(9, 8, 6, 5, 3, 2, 0)
        );

        /* 추가 3건 입찰 & 낙찰 */
        bid(auctions[1], auctions[4], auctions[7]);
        makeAuctionEnd(auctions[1], auctions[4], auctions[7]);

        WinningAuctionArtAssembler assembler2 = memberInformationService.getWinningAuctionArts(bidder.getId());
        assertThatWinningAuctionMatch(
                assembler2.result(),
                List.of(9, 8, 7, 6, 5, 4, 3, 2, 1, 0)
        );
    }

    private void bid(Auction... auctions) {
        for (Auction auction : auctions) {
            auction.applyNewBid(bidder, auction.getHighestBidPrice());
        }
    }

    private void makeAuctionEnd(Auction... auctions) {
        for (Auction auction : auctions) {
            em.createQuery("UPDATE Auction a" +
                            " SET a.period.endDate = :endDate" +
                            " WHERE a.id = :auctionId")
                    .setParameter("endDate", LocalDateTime.now().minusDays(1))
                    .setParameter("auctionId", auction.getId())
                    .executeUpdate();
        }
    }

    private void assertThatWinningAuctionMatch(List<AuctionArt> result, List<Integer> indices) {
        int totalSize = indices.size();
        assertThat(result).hasSize(totalSize);

        for (int i = 0; i < totalSize; i++) {
            int index = indices.get(i);

            AuctionArt auctionArt = result.get(i);
            Auction auction = auctions[index];
            Art art = arts[index];

            assertAll(
                    () -> assertThat(auctionArt.getAuction().id()).isEqualTo(auction.getId()),
                    () -> assertThat(auctionArt.getAuction().highestBidPrice()).isEqualTo(auction.getHighestBidPrice()),

                    () -> assertThat(auctionArt.getArt().getId()).isEqualTo(art.getId()),
                    () -> assertThat(auctionArt.getArt().getName()).isEqualTo(art.getNameValue()),
                    () -> assertThat(auctionArt.getArt().getDescription()).isEqualTo(art.getDescriptionValue()),
                    () -> assertThat(auctionArt.getArt().getPrice()).isEqualTo(art.getPrice()),
                    () -> assertThat(auctionArt.getArt().getStatus()).isEqualTo(art.getStatus().getDescription()),
                    () -> assertThat(auctionArt.getArt().getStorageName()).isEqualTo(art.getStorageName()),
                    () -> assertThat(auctionArt.getArt().getHashtags()).containsExactlyInAnyOrderElementsOf(art.getHashtags()),

                    () -> assertThat(auctionArt.getOwner().id()).isEqualTo(owner.getId()),
                    () -> assertThat(auctionArt.getOwner().nickname()).isEqualTo(owner.getNicknameValue()),
                    () -> assertThat(auctionArt.getOwner().school()).isEqualTo(owner.getSchool()),

                    () -> assertThat(auctionArt.getHighestBidder().id()).isEqualTo(bidder.getId()),
                    () -> assertThat(auctionArt.getHighestBidder().nickname()).isEqualTo(bidder.getNicknameValue()),
                    () -> assertThat(auctionArt.getHighestBidder().school()).isEqualTo(bidder.getSchool())
            );
        }
    }
}
