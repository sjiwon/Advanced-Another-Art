package com.sjiwon.anotherart.art.application;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.model.ArtName;
import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.common.ServiceTest;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_2;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Art [Service Layer] -> ArtValidator 테스트")
class ArtValidatorTest extends ServiceTest {
    @Autowired
    private ArtValidator artValidator;

    private Member owner;
    private Art art;

    @BeforeEach
    void setUp() {
        owner = memberRepository.save(MEMBER_A.toMember());
        art = artRepository.save(AUCTION_1.toArt(owner));
    }

    @Test
    @DisplayName("작품명 중복에 대한 검증을 진행한다 [스터디 생성 과정]")
    void validateUniqueNameForCreate() {
        final ArtName same = art.getName();
        final ArtName diff = ArtName.from("diff" + same.getValue());

        assertThatThrownBy(() -> artValidator.validateUniqueNameForCreate(same))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(ArtErrorCode.DUPLICATE_NAME.getMessage());
        assertDoesNotThrow(() -> artValidator.validateUniqueNameForCreate(diff));
    }

    @Test
    @DisplayName("작품명 중복에 대한 검증을 진행한다 [스터디 수정 과정]")
    void validateUniqueNameForUpdate() {
        // given
        final Art another = artRepository.save(AUCTION_2.toArt(owner));

        // when - then
        final ArtName otherName = another.getName();
        final ArtName myName = art.getName();

        assertThatThrownBy(() -> artValidator.validateUniqueNameForUpdate(otherName, art.getId()))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(ArtErrorCode.DUPLICATE_NAME.getMessage());
        assertDoesNotThrow(() -> artValidator.validateUniqueNameForUpdate(myName, art.getId()));
    }
}
