package com.sjiwon.anotherart.art.domain;

import com.sjiwon.anotherart.common.RepositoryTest;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

import static org.assertj.core.api.Assertions.assertThat;

class ArtRepositoryTest extends RepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ArtRepository artRepository;

    @Test
    @DisplayName("작품 소유자인지 검증한다")
    void test(){
        // given
        Member memberA = createMemberA();
        Member memberB = createMemberB();

        // when
        Art art = createArt(memberA);

        // then
        assertThat(art.isArtOwner(memberA.getId())).isTrue();
        assertThat(art.isArtOwner(memberB.getId())).isFalse();
    }

    private Member createMemberA() {
        return memberRepository.save(MemberFixture.A.toMember(PasswordEncoderFactories.createDelegatingPasswordEncoder()));
    }

    private Member createMemberB() {
        return memberRepository.save(MemberFixture.B.toMember(PasswordEncoderFactories.createDelegatingPasswordEncoder()));
    }

    private Art createArt(Member member) {
        return artRepository.save(ArtFixture.A.toArt(member));
    }
}