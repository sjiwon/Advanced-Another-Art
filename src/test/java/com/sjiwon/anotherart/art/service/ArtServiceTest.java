package com.sjiwon.anotherart.art.service;

import com.sjiwon.anotherart.art.controller.utils.ArtRegistrationRequestUtils;
import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtStatus;
import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.art.service.dto.request.ArtRegisterRequestDto;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.auction.domain.Period;
import com.sjiwon.anotherart.auction.domain.record.AuctionRecord;
import com.sjiwon.anotherart.common.ServiceIntegrateTest;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.sjiwon.anotherart.common.utils.ArtUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Art [Service Layer] -> ArtService 테스트")
@RequiredArgsConstructor
class ArtServiceTest extends ServiceIntegrateTest {
    private final ArtService artService;

    @Test
    @DisplayName("일반 작품을 등록한다")
    void test1() throws Exception {
        // given
        Member member = createMemberA();
        ArtFixture generalArtFixture = ArtFixture.B;
        ArtRegisterRequestDto request =
                ArtRegistrationRequestUtils.createGeneralArtRequest(generalArtFixture, HASHTAGS).toGeneralArtDto();

        // when
        artService.artRegistration(member.getId(), request);

        // then
        List<Art> ownerArts = artRepository.findByOwnerId(member.getId());
        assertThat(ownerArts.size()).isEqualTo(1);

        Art art = ownerArts.get(0);
        assertAll(
                () -> assertThat(art.getName()).isEqualTo(generalArtFixture.getName()),
                () -> assertThat(art.getDescription()).isEqualTo(generalArtFixture.getDescription()),
                () -> assertThat(art.getUploadName()).isEqualTo(generalArtFixture.getUploadName())
        );
    }

    @Test
    @DisplayName("경매 작품을 등록한다")
    void test2() throws Exception {
        // given
        Member member = createMemberA();
        ArtFixture auctionArtFixture = ArtFixture.A;
        ArtRegisterRequestDto request =
                ArtRegistrationRequestUtils.createAuctionArtRequest(auctionArtFixture, HASHTAGS).toAuctionArtDto();

        // when
        artService.artRegistration(member.getId(), request);

        // then
        List<Art> ownerArts = artRepository.findByOwnerId(member.getId());
        assertThat(ownerArts.size()).isEqualTo(1);

        Art art = ownerArts.get(0);
        assertAll(
                () -> assertThat(art.getName()).isEqualTo(auctionArtFixture.getName()),
                () -> assertThat(art.getDescription()).isEqualTo(auctionArtFixture.getDescription()),
                () -> assertThat(art.getUploadName()).isEqualTo(auctionArtFixture.getUploadName())
        );

        Auction auction = auctionRepository.findByArtId(art.getId()).orElseThrow();
        assertAll(
                () -> assertThat(auction.getBidder()).isNull(),
                () -> assertThat(auction.getBidAmount()).isEqualTo(art.getPrice())
        );
    }

    @Test
    @DisplayName("작품명 중복 체크 로직을 진행한다")
    void test3() {
        // given
        Member owner = createMemberA();
        Art art = createGeneralArt(owner);

        // when - then
        assertDoesNotThrow(() -> artService.artNameDuplicateCheck(art.getName() + "hello"));
        assertThatThrownBy(() -> artService.artNameDuplicateCheck(art.getName()))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(ArtErrorCode.INVALID_ART_NAME.getMessage());
    }

    @Test
    @DisplayName("작품 설명을 수정한다")
    void test4() {
        // given
        Member owner = createMemberA();
        Art art = createGeneralArt(owner);

        // when
        assertThatThrownBy(() -> artService.changeDescription(art.getId(), art.getDescription()))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(ArtErrorCode.NAME_SAME_AS_BEFORE.getMessage());

        final String changeDescription = art.getDescription() + "change";
        artService.changeDescription(art.getId(), changeDescription);

        // then
        assertThat(art.getDescription()).isEqualTo(changeDescription);
    }

    @Test
    @DisplayName("작품의 해시태그를 업데이트한다")
    void test5() {
        // given
        Member owner = createMemberA();
        Art art = createGeneralArt(owner);

        // when
        artService.updateHashtags(art.getId(), UPDATE_HASHTAGS);

        // then
        assertThat(art.getHashtagList()).containsAll(UPDATE_HASHTAGS);
    }

    @Nested
    @DisplayName("작품을 삭제한다")
    class deleteArt {
        @Test
        @DisplayName("작품 소유자가 아니면 삭제를 할 수 없다")
        void test1() {
            // given
            Member owner = createMemberA();
            Art art = createGeneralArt(owner);

            // when - then
            assertThatThrownBy(() -> artService.deleteArt(art.getId(), owner.getId() + 100L))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(ArtErrorCode.INVALID_ART_DELETE_BY_ANONYMOUS.getMessage());
        }

        @Test
        @DisplayName("이미 판매된 작품은 삭제가 불가능하다")
        void test2() {
            // given
            Member owner = createMemberA();
            Art art = createGeneralArt(owner);
            art.changeArtStatus(ArtStatus.SOLD_OUT);

            // when - then
            assertThatThrownBy(() -> artService.deleteArt(art.getId(), owner.getId()))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(ArtErrorCode.ALREADY_SOLD_OUT.getMessage());
        }

        @Test
        @DisplayName("경매 작품일 경우 입찰 기록이 존재하면 삭제가 불가능하다")
        void test3() {
            Member owner = createMemberA();
            Art art = createAuctionArt(owner);

            Member bidder = createMemberB();
            proceedingBid(art, bidder, art.getPrice() + 5_000);

            // when - then
            assertThatThrownBy(() -> artService.deleteArt(art.getId(), owner.getId()))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(ArtErrorCode.ALREADY_BID_EXISTS.getMessage());
        }

        @Test
        @DisplayName("삭제에 성공한다")
        void test4() {
            // given
            Member owner = createMemberA();
            Art art = createGeneralArt(owner);

            // when
            artService.deleteArt(art.getId(), owner.getId());

            // then
            assertThat(artRepository.findById(art.getId())).isEmpty();
        }
    }

    private void proceedingBid(Art art, Member bidder, int bidAmount) {
        Auction auction = auctionRepository.findByArtId(art.getId()).orElseThrow();
        auctionRecordRepository.save(AuctionRecord.createAuctionRecord(auction, bidder, bidAmount));
    }

    private Member createMemberA() {
        return memberRepository.save(MemberFixture.A.toMember());
    }

    private Member createMemberB() {
        return memberRepository.save(MemberFixture.B.toMember());
    }

    private Art createGeneralArt(Member owner) {
        return artRepository.save(ArtFixture.B.toArt(owner, HASHTAGS));
    }

    private Art createAuctionArt(Member owner) {
        Art art = artRepository.save(ArtFixture.A.toArt(owner, HASHTAGS));
        auctionRepository.save(Auction.initAuction(art, Period.of(currentTime1DayLater, currentTime3DayLater)));
        return art;
    }
}