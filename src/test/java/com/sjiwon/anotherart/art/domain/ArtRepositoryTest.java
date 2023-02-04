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

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Art [Repository Layer] -> ArtRepository 테스트")
class ArtRepositoryTest extends RepositoryTest {
    @Autowired
    ArtRepository artRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("작품 소유자인지 검증한다")
    void test1(){
        // given
        Member owner = createMemberA();
        Member memberB = createMemberB();

        // when
        Art art = createArt(owner);

        // then
        assertThat(art.isArtOwner(owner.getId())).isTrue();
        assertThat(art.isArtOwner(memberB.getId())).isFalse();
    }
    
    @Test
    @DisplayName("사용자 ID를 통해서 본인 소유의 작품 리스트를 조회한다")
    void test2() {
        // given
        Member owner = createMemberA();
        Art art = createArt(owner);
        
        // when
        List<Art> ownerArtList = artRepository.findByOwnerId(owner.getId());
        assertThat(ownerArtList.size()).isEqualTo(1);

        // then
        Art findArt = ownerArtList.get(0);
        assertThat(findArt.getId()).isEqualTo(art.getId());
        assertThat(findArt.getName()).isEqualTo(art.getName());
        assertThat(findArt.getPrice()).isEqualTo(art.getPrice());
        assertThat(findArt.getArtType()).isEqualTo(art.getArtType());
        assertThat(findArt.getArtStatus()).isEqualTo(ArtStatus.FOR_SALE);

        Member findOwner = findArt.getOwner();
        assertThat(findOwner.getId()).isEqualTo(owner.getId());
        assertThat(findOwner.getName()).isEqualTo(owner.getName());
        assertThat(findOwner.getNickname()).isEqualTo(owner.getNickname());
    }

    @Test
    @DisplayName("작품명 중복 여부를 확인한다")
    void test3() {
        // given
        Member owner = createMemberA();
        Art art = createArt(owner);

        // when
        boolean actual1 = artRepository.existsByName(art.getName());
        boolean actual2 = artRepository.existsByName(art.getName() + "fake");

        // then
        assertThat(actual1).isTrue();
        assertThat(actual2).isFalse();
    }

    private Member createMemberA() {
        return memberRepository.save(MemberFixture.A.toMember());
    }

    private Member createMemberB() {
        return memberRepository.save(MemberFixture.B.toMember());
    }

    private Art createArt(Member member) {
        return artRepository.save(ArtFixture.A.toArt(member));
    }
}