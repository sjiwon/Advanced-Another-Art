package com.sjiwon.anotherart.art.domain;

import com.sjiwon.anotherart.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static com.sjiwon.anotherart.art.domain.ArtStatus.ON_SALE;
import static com.sjiwon.anotherart.art.domain.ArtStatus.SOLD;
import static com.sjiwon.anotherart.art.domain.ArtType.AUCTION;
import static com.sjiwon.anotherart.art.domain.ArtType.GENERAL;
import static com.sjiwon.anotherart.fixture.ArtFixture.AUCTION_A;
import static com.sjiwon.anotherart.fixture.ArtFixture.GENERAL_A;
import static com.sjiwon.anotherart.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.fixture.MemberFixture.MEMBER_B;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Art 도메인 테스트")
class ArtTest {
    private Member owner;
    private Member member;

    @BeforeEach
    void setUp() {
        owner = MEMBER_A.toMember();
        member = MEMBER_B.toMember();
        ReflectionTestUtils.setField(owner, "id", 1L);
        ReflectionTestUtils.setField(member, "id", 2L);
    }

    @Test
    @DisplayName("Art를 생성한다")
    void construct(){
        Art auctionArt = AUCTION_A.toArt(owner);
        Art generalArt = GENERAL_A.toArt(owner);

        assertAll(
                () -> assertThat(auctionArt.getNameValue()).isEqualTo(AUCTION_A.getName()),
                () -> assertThat(auctionArt.getDescriptionValue()).isEqualTo(AUCTION_A.getDescription()),
                () -> assertThat(auctionArt.getType()).isEqualTo(AUCTION),
                () -> assertThat(auctionArt.getStorageName()).isEqualTo(AUCTION_A.getStorageName()),
                () -> assertThat(auctionArt.getPrice()).isEqualTo(AUCTION_A.getPrice()),
                () -> assertThat(auctionArt.getStatus()).isEqualTo(ON_SALE),
                () -> assertThat(auctionArt.getHashtags()).containsExactlyInAnyOrderElementsOf(AUCTION_A.getHashtags()),
                () -> assertThat(auctionArt.getOwner().getName()).isEqualTo(owner.getName()),
                () -> assertThat(auctionArt.getOwner().getNickname()).isEqualTo(owner.getNickname()),
                () -> assertThat(auctionArt.getOwner().getLoginId()).isEqualTo(owner.getLoginId()),

                () -> assertThat(generalArt.getNameValue()).isEqualTo(GENERAL_A.getName()),
                () -> assertThat(generalArt.getDescriptionValue()).isEqualTo(GENERAL_A.getDescription()),
                () -> assertThat(generalArt.getType()).isEqualTo(GENERAL),
                () -> assertThat(generalArt.getStorageName()).isEqualTo(GENERAL_A.getStorageName()),
                () -> assertThat(generalArt.getPrice()).isEqualTo(GENERAL_A.getPrice()),
                () -> assertThat(generalArt.getStatus()).isEqualTo(ON_SALE),
                () -> assertThat(generalArt.getHashtags()).containsExactlyInAnyOrderElementsOf(GENERAL_A.getHashtags()),
                () -> assertThat(generalArt.getOwner().getName()).isEqualTo(owner.getName()),
                () -> assertThat(generalArt.getOwner().getNickname()).isEqualTo(owner.getNickname()),
                () -> assertThat(generalArt.getOwner().getLoginId()).isEqualTo(owner.getLoginId())
        );
    }

    @Test
    @DisplayName("작품을 수정한다 [이름 & 설명 & 해시태그]")
    void update(){
        // given
        Art art = AUCTION_A.toArt(owner);

        // when
        final ArtName updateName = ArtName.from("HELLO ART");
        final Description updateDescription = Description.from("Hello World");
        final Set<String> updateHashtags = Set.of("HELLO", "WORLD", "JAVA", "SPRING");
        art.update(updateName, updateDescription, updateHashtags);

        // then
        assertAll(
                () -> assertThat(art.getNameValue()).isEqualTo("HELLO ART"),
                () -> assertThat(art.getDescriptionValue()).isEqualTo("Hello World"),
                () -> assertThat(art.getHashtags()).containsExactlyInAnyOrderElementsOf(updateHashtags)
        );
    }

    @Test
    @DisplayName("작품 판매를 종료한다")
    void closeSale(){
        // given
        Art art = AUCTION_A.toArt(owner);
        assertThat(art.getStatus()).isEqualTo(ON_SALE);

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
        Art auctionArt = AUCTION_A.toArt(owner);
        Art generalArt = GENERAL_A.toArt(owner);

        // when
        boolean actual1 = auctionArt.isAuctionType();
        boolean actual2 = generalArt.isAuctionType();

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("작품 소유자인지 확인한다")
    void isArtOwner() {
        // given
        Art art = AUCTION_A.toArt(owner);

        // when
        boolean actual1 = art.isArtOwner(owner.getId());
        boolean actual2 = art.isArtOwner(member.getId());

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
