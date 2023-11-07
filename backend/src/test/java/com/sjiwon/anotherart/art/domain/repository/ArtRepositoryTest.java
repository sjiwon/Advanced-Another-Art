package com.sjiwon.anotherart.art.domain.repository;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.model.ArtType;
import com.sjiwon.anotherart.common.RepositoryTest;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.sjiwon.anotherart.art.domain.model.ArtType.AUCTION;
import static com.sjiwon.anotherart.art.domain.model.ArtType.GENERAL;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_2;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.GENERAL_1;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.DUMMY_1;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Art -> ArtRepository 테스트")
class ArtRepositoryTest extends RepositoryTest {
    @Autowired
    private ArtRepository sut;

    @Autowired
    private MemberRepository memberRepository;

    private Member owner;
    private Member anonymous;

    @BeforeEach
    void setUp() {
        owner = memberRepository.save(MEMBER_A.toMember());
        anonymous = memberRepository.save(DUMMY_1.toMember());
    }

    @Test
    @DisplayName("작품 타입을 조회한다")
    void getArtTypeById() {
        // given
        final Art generalArt = sut.save(GENERAL_1.toArt(owner));
        final Art auctionArt = sut.save(AUCTION_1.toArt(owner));

        // when
        final ArtType general = sut.getArtTypeById(generalArt.getId());
        final ArtType auction = sut.getArtTypeById(auctionArt.getId());

        // then
        assertAll(
                () -> assertThat(general).isEqualTo(GENERAL),
                () -> assertThat(auction).isEqualTo(AUCTION)
        );
    }

    @Test
    @DisplayName("작품 주인인지 확인한다")
    void isOwner() {
        // given
        final Art art = sut.save(GENERAL_1.toArt(owner));

        // when
        final boolean actual1 = sut.isOwner(art.getId(), owner.getId());
        final boolean actual2 = sut.isOwner(art.getId(), anonymous.getId());

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("이름에 해당하는 작품이 존재하는지 확인한다")
    void existsByNameValue() {
        // given
        final Art art = sut.save(GENERAL_1.toArt(owner));
        final String same = art.getName().getValue();
        final String diff = "diff" + art.getName().getValue();

        // when
        final boolean actual1 = sut.existsByNameValue(same);
        final boolean actual2 = sut.existsByNameValue(diff);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("해당 작품명을 사용하는 다른 작품이 존재하는지 확인한다")
    void isNameUsedByOther() {
        // given
        final Art artA = sut.save(AUCTION_1.toArt(owner));
        final Art artB = sut.save(AUCTION_2.toArt(owner));

        // when
        final boolean actual1 = sut.isNameUsedByOther(artA.getId(), artA.getName().getValue());
        final boolean actual2 = sut.isNameUsedByOther(artA.getId(), artB.getName().getValue());
        final boolean actual3 = sut.isNameUsedByOther(artB.getId(), artB.getName().getValue());
        final boolean actual4 = sut.isNameUsedByOther(artB.getId(), artA.getName().getValue());

        // then
        assertAll(
                () -> assertThat(actual1).isFalse(),
                () -> assertThat(actual2).isTrue(),
                () -> assertThat(actual3).isFalse(),
                () -> assertThat(actual4).isTrue()
        );
    }
}
