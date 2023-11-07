package com.sjiwon.anotherart.art.domain.model;

import com.sjiwon.anotherart.member.domain.model.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.sjiwon.anotherart.art.domain.model.ArtStatus.ON_SALE;
import static com.sjiwon.anotherart.art.domain.model.ArtStatus.SOLD;
import static com.sjiwon.anotherart.art.domain.model.ArtType.AUCTION;
import static com.sjiwon.anotherart.art.domain.model.ArtType.GENERAL;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.GENERAL_1;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_B;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Art -> 도메인 [Art] 테스트")
class ArtTest {
    private final Member owner = MEMBER_A.toMember().apply(1L);
    private final Member member = MEMBER_B.toMember().apply(2L);

    @Test
    @DisplayName("Art를 생성한다")
    void construct() {
        final Art auctionArt = AUCTION_1.toArt(owner);
        final Art generalArt = GENERAL_1.toArt(owner);

        assertAll(
                () -> assertThat(auctionArt.getName().getValue()).isEqualTo(AUCTION_1.getName().getValue()),
                () -> assertThat(auctionArt.getDescription().getValue()).isEqualTo(AUCTION_1.getDescription().getValue()),
                () -> assertThat(auctionArt.getType()).isEqualTo(AUCTION),
                () -> assertThat(auctionArt.getPrice()).isEqualTo(AUCTION_1.getPrice()),
                () -> assertThat(auctionArt.getUploadImage().getUploadFileName()).isEqualTo(AUCTION_1.getUploadImage().getUploadFileName()),
                () -> assertThat(auctionArt.getUploadImage().getLink()).isEqualTo(AUCTION_1.getUploadImage().getLink()),
                () -> assertThat(auctionArt.getStatus()).isEqualTo(ON_SALE),
                () -> assertThat(auctionArt.getHashtags()).containsExactlyInAnyOrderElementsOf(AUCTION_1.getHashtags()),
                () -> assertThat(auctionArt.getOwner().getId()).isEqualTo(owner.getId()),

                () -> assertThat(generalArt.getName().getValue()).isEqualTo(GENERAL_1.getName().getValue()),
                () -> assertThat(generalArt.getDescription().getValue()).isEqualTo(GENERAL_1.getDescription().getValue()),
                () -> assertThat(generalArt.getType()).isEqualTo(GENERAL),
                () -> assertThat(generalArt.getPrice()).isEqualTo(GENERAL_1.getPrice()),
                () -> assertThat(generalArt.getUploadImage().getUploadFileName()).isEqualTo(GENERAL_1.getUploadImage().getUploadFileName()),
                () -> assertThat(generalArt.getUploadImage().getLink()).isEqualTo(GENERAL_1.getUploadImage().getLink()),
                () -> assertThat(generalArt.getStatus()).isEqualTo(ON_SALE),
                () -> assertThat(generalArt.getHashtags()).containsExactlyInAnyOrderElementsOf(GENERAL_1.getHashtags()),
                () -> assertThat(generalArt.getOwner().getId()).isEqualTo(owner.getId())
        );
    }

    @Test
    @DisplayName("작품을 수정한다 [이름 & 설명 & 해시태그]")
    void update() {
        // given
        final Art art = AUCTION_1.toArt(owner);
        final ArtName updateName = ArtName.from("HELLO ART");
        final Description updateDescription = Description.from("Hello World");
        final Set<String> updateHashtags = Set.of("HELLO", "WORLD", "JAVA", "SPRING");

        // when
        art.update(updateName, updateDescription, updateHashtags);

        // then
        assertAll(
                () -> assertThat(art.getName().getValue()).isEqualTo(updateName.getValue()),
                () -> assertThat(art.getDescription().getValue()).isEqualTo(updateDescription.getValue()),
                () -> assertThat(art.getHashtags()).containsExactlyInAnyOrderElementsOf(updateHashtags)
        );
    }

    @Test
    @DisplayName("작품 판매를 종료한다")
    void closeSale() {
        // given
        final Art art = AUCTION_1.toArt(owner);
        assertAll(
                () -> assertThat(art.getStatus()).isEqualTo(ON_SALE),
                () -> assertThat(art.isSold()).isFalse()
        );

        // when
        art.closeSale();

        // then
        assertAll(
                () -> assertThat(art.getStatus()).isEqualTo(SOLD),
                () -> assertThat(art.isSold()).isTrue()
        );
    }

    @Test
    @DisplayName("경매 작품인지 확인한다")
    void isAuctionType() {
        // given
        final Art auctionArt = AUCTION_1.toArt(owner);
        final Art generalArt = GENERAL_1.toArt(owner);

        // when
        final boolean actual1 = auctionArt.isAuctionType();
        final boolean actual2 = generalArt.isAuctionType();

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("작품 소유자인지 확인한다")
    void isOwner() {
        // given
        final Art art = AUCTION_1.toArt(owner);

        // when
        final boolean actual1 = art.isOwner(owner);
        final boolean actual2 = art.isOwner(member);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
