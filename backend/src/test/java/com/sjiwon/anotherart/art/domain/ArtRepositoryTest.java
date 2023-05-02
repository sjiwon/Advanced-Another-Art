package com.sjiwon.anotherart.art.domain;

import com.sjiwon.anotherart.common.RepositoryTest;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.sjiwon.anotherart.common.utils.ArtUtils.HASHTAGS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Art [Repository Layer] -> ArtRepository 테스트")
class ArtRepositoryTest extends RepositoryTest {
    @Autowired
    private ArtRepository artRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("작품 소유자인지 검증한다")
    void test1(){
        // given
        Member owner = createMemberA();
        Member memberB = createMemberB();

        // when
        Art art = createAuctionArt(owner);

        // then
        assertAll(
                () -> assertThat(art.isArtOwner(owner.getId())).isTrue(),
                () -> assertThat(art.isArtOwner(memberB.getId())).isFalse()
        );
    }
    
    @Test
    @DisplayName("사용자 ID를 통해서 본인 소유의 작품 리스트를 조회한다")
    void test2() {
        // given
        Member owner = createMemberA();
        Art artA = createAuctionArt(owner);
        Art artB = createGeneralArt(owner);

        // when
        List<Art> ownerArtList = artRepository.findByOwnerId(owner.getId());

        // then
        assertThat(ownerArtList.size()).isEqualTo(2);
        
        Art findArtA = ownerArtList.get(0);
        assertAll(
                () -> assertThat(findArtA.getId()).isEqualTo(artA.getId()),
                () -> assertThat(findArtA.getName()).isEqualTo(artA.getName()),
                () -> assertThat(findArtA.getPrice()).isEqualTo(artA.getPrice()),
                () -> assertThat(findArtA.getArtType()).isEqualTo(artA.getArtType()),
                () -> assertThat(findArtA.getArtStatus()).isEqualTo(ArtStatus.FOR_SALE)
        );

        Art findArtB = ownerArtList.get(1);
        assertAll(
                () -> assertThat(findArtB.getId()).isEqualTo(artB.getId()),
                () -> assertThat(findArtB.getName()).isEqualTo(artB.getName()),
                () -> assertThat(findArtB.getPrice()).isEqualTo(artB.getPrice()),
                () -> assertThat(findArtB.getArtType()).isEqualTo(artB.getArtType()),
                () -> assertThat(findArtB.getArtStatus()).isEqualTo(ArtStatus.FOR_SALE)
        );
    }

    @Test
    @DisplayName("작품명 중복 여부를 확인한다")
    void test3() {
        // given
        Member owner = createMemberA();
        Art art = createAuctionArt(owner);

        // when
        boolean actual1 = artRepository.existsByName(art.getName());
        boolean actual2 = artRepository.existsByName(art.getName() + "fake");

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("작품의 해시태그를 삭제한다")
    void test4() {
        // given
        Member owner = createMemberA();
        Art art = createAuctionArt(owner);

        // when
        artRepository.deleteHashtagsByArtId(art.getId());

        // then
        Art findArt = artRepository.findById(art.getId()).orElseThrow();
        assertThat(findArt.getHashtags()).isEmpty();
    }

    @Test
    @DisplayName("작품의 타입을 조회한다")
    void test5() {
        // given
        Member owner = createMemberA();
        Art generalArt = createGeneralArt(owner);
        Art auctionArt = createAuctionArt(owner);

        // when
        ArtType generalType = artRepository.searchArtType(generalArt.getId());
        ArtType auctionType = artRepository.searchArtType(auctionArt.getId());

        // then
        assertAll(
                () -> assertThat(generalType).isEqualTo(ArtType.GENERAL),
                () -> assertThat(auctionType).isEqualTo(ArtType.AUCTION)
        );
    }

    private Member createMemberA() {
        return memberRepository.save(MemberFixture.A.toMember());
    }

    private Member createMemberB() {
        return memberRepository.save(MemberFixture.B.toMember());
    }

    private Art createAuctionArt(Member member) {
        return artRepository.save(ArtFixture.A.toArt(member, HASHTAGS));
    }

    private Art createGeneralArt(Member member) {
        return artRepository.save(ArtFixture.B.toArt(member, HASHTAGS));
    }
}
