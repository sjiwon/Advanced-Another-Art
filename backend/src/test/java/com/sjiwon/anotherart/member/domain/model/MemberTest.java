package com.sjiwon.anotherart.member.domain.model;

import com.sjiwon.anotherart.common.mock.fake.FakePasswordEncryptor;
import com.sjiwon.anotherart.global.encrypt.PasswordEncryptor;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_B;
import static com.sjiwon.anotherart.member.domain.model.Role.USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member -> 도메인 [Member] 테스트")
class MemberTest {
    private final PasswordEncryptor passwordEncryptor = new FakePasswordEncryptor();

    @Test
    @DisplayName("멤버를 생성한다")
    void construct() {
        final Member member = MEMBER_A.toMember();

        assertAll(
                () -> assertThat(member.getName()).isEqualTo(MEMBER_A.getName()),
                () -> assertThat(member.getNickname().getValue()).isEqualTo(MEMBER_A.getNickname().getValue()),
                () -> assertThat(member.getLoginId()).isEqualTo(MEMBER_A.getLoginId()),
                () -> assertThat(passwordEncryptor.matches(MEMBER_A.getPassword(), member.getPassword().getValue())).isTrue(),
                () -> assertThat(member.getEmail().getValue()).isEqualTo(MEMBER_A.getEmail().getValue()),
                () -> assertThat(member.getAddress().getPostcode()).isEqualTo(MEMBER_A.getAddress().getPostcode()),
                () -> assertThat(member.getAddress().getDefaultAddress()).isEqualTo(MEMBER_A.getAddress().getDefaultAddress()),
                () -> assertThat(member.getAddress().getDetailAddress()).isEqualTo(MEMBER_A.getAddress().getDetailAddress()),
                () -> assertThat(member.getTotalPoint()).isEqualTo(0),
                () -> assertThat(member.getAvailablePoint()).isEqualTo(0),
                () -> assertThat(member.getRole()).isEqualTo(USER)
        );
    }

    @Nested
    @DisplayName("닉네임 변경")
    class ChangeNickname {
        @Test
        @DisplayName("이전과 동일한 닉네임으로 변경할 수 없다")
        void throwExceptionByNicknameSameAsBefore() {
            // given
            final Member member = MEMBER_A.toMember().apply(1L);
            final String oldNickname = member.getNickname().getValue();

            // when - then
            assertThatThrownBy(() -> member.changeNickname(oldNickname))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(MemberErrorCode.NICKNAME_SAME_AS_BEFORE.getMessage());
        }

        @Test
        @DisplayName("닉네임 변경에 성공한다")
        void success() {
            // given
            final Member member = MEMBER_A.toMember().apply(1L);
            final String newNickname = member.getNickname().getValue() + "diff";

            // when
            member.changeNickname(newNickname);

            // then
            assertThat(member.getNickname().getValue()).isEqualTo(newNickname);
        }
    }

    @Nested
    @DisplayName("비밀번호 변경")
    class ChangePassword {
        @Test
        @DisplayName("이전과 동일한 비밀번호호 변경할 수 없다")
        void throwExceptionByPasswordSameAsBefore() {
            // given
            final Member member = MEMBER_A.toMember().apply(1L);
            final String oldPassword = MEMBER_A.getPassword();

            // when - then
            assertThatThrownBy(() -> member.changePassword(oldPassword, passwordEncryptor))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(MemberErrorCode.PASSWORD_SAME_AS_BEFORE.getMessage());
        }

        @Test
        @DisplayName("비밀번호 변경에 성공한다")
        void success() {
            // given
            final Member member = MEMBER_A.toMember().apply(1L);
            final String oldPassword = MEMBER_A.getPassword();
            final String newPassword = MEMBER_A.getPassword() + "diff";

            // when
            member.changePassword(newPassword, passwordEncryptor);

            // then
            assertAll(
                    () -> assertThat(passwordEncryptor.matches(oldPassword, member.getPassword().getValue())).isFalse(),
                    () -> assertThat(passwordEncryptor.matches(newPassword, member.getPassword().getValue())).isTrue()
            );
        }
    }

    @Test
    @DisplayName("주소를 변경한다")
    void changeAddress() {
        // given
        final Member member = MEMBER_A.toMember().apply(1L);
        final int changePostcode = 98765;
        final String changeDefault = "성남";
        final String changeDetail = "카카오";

        // when
        member.changeAddress(changePostcode, changeDefault, changeDetail);

        // then
        assertAll(
                () -> assertThat(member.getAddress().getPostcode()).isEqualTo(changePostcode),
                () -> assertThat(member.getAddress().getDefaultAddress()).isEqualTo(changeDefault),
                () -> assertThat(member.getAddress().getDetailAddress()).isEqualTo(changeDetail)
        );
    }

    @Nested
    @DisplayName("전체 포인트 증가 [포인트 충전 / 작품 판매]")
    class IncreaseTotalPoint {
        @Test
        @DisplayName("전체 포인트를 증가시킨다")
        void success() {
            // given
            final Member member = MEMBER_A.toMember().apply(1L);

            assertAll(
                    () -> assertThat(member.getTotalPoint()).isEqualTo(0),
                    () -> assertThat(member.getAvailablePoint()).isEqualTo(0)
            );

            // when
            member.increaseTotalPoint(50_000);

            // then
            assertAll(
                    () -> assertThat(member.getTotalPoint()).isEqualTo(50_000),
                    () -> assertThat(member.getAvailablePoint()).isEqualTo(50_000)
            );
        }
    }

    @Nested
    @DisplayName("전체 포인트 감소 [포인트 환불 / 작품 구매]")
    class DecreaseTotalPoint {
        @Test
        @DisplayName("사용 가능한 포인트가 충분하지 않음에 따라 감소시킬 수 없다")
        void throwExceptionByPointIsNotEnough() {
            // given
            final Member member = MEMBER_A.toMember().apply(1L);

            // when - then
            assertThatThrownBy(() -> member.decreaseTotalPoint(5_000))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(MemberErrorCode.POINT_IS_NOT_ENOUGH.getMessage());
        }

        @Test
        @DisplayName("전체 포인트를 감소시킨다")
        void success() {
            // given
            final Member member = MEMBER_A.toMember().apply(1L);
            member.increaseTotalPoint(50_000);

            assertAll(
                    () -> assertThat(member.getTotalPoint()).isEqualTo(50_000),
                    () -> assertThat(member.getAvailablePoint()).isEqualTo(50_000)
            );

            // when
            member.decreaseTotalPoint(30_000);

            // then
            assertAll(
                    () -> assertThat(member.getTotalPoint()).isEqualTo(50_000 - 30_000),
                    () -> assertThat(member.getAvailablePoint()).isEqualTo(50_000 - 30_000)
            );
        }
    }

    @Nested
    @DisplayName("사용 가능한 포인트 증가 [경매 작품 최고 입찰자에서 물러났을 경우]")
    class IncreaseAvailablePoint {
        @Test
        @DisplayName("사용 가능한 포인트를 증가시킨다")
        void success() {
            // given
            final Member member = MEMBER_A.toMember().apply(1L);
            member.increaseTotalPoint(100_000);
            member.decreaseAvailablePoint(50_000);

            assertAll(
                    () -> assertThat(member.getTotalPoint()).isEqualTo(100_000),
                    () -> assertThat(member.getAvailablePoint()).isEqualTo(100_000 - 50_000)
            );

            // when
            member.increaseAvailablePoint(30_000);

            // then
            assertAll(
                    () -> assertThat(member.getTotalPoint()).isEqualTo(100_000),
                    () -> assertThat(member.getAvailablePoint()).isEqualTo(100_000 - 50_000 + 30_000)
            );
        }
    }

    @Nested
    @DisplayName("사용 가능한 포인트 감소 [경매 작품 입찰 참여]")
    class DecreaseAvailablePoint {
        @Test
        @DisplayName("사용 가능한 포인트가 충분하지 않음에 따라 감소시킬 수 없다")
        void throwExceptionByPointIsNotEnough() {
            // given
            final Member member = MEMBER_A.toMember().apply(1L);
            member.increaseTotalPoint(100_000);
            member.decreaseAvailablePoint(30_000);

            // when - then
            assertThatThrownBy(() -> member.decreaseAvailablePoint(80_000))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(MemberErrorCode.POINT_IS_NOT_ENOUGH.getMessage());
        }

        @Test
        @DisplayName("사용 가능한 포인트를 감소시킨다")
        void success() {
            // given
            final Member member = MEMBER_A.toMember().apply(1L);
            member.increaseTotalPoint(100_000);

            // when
            member.decreaseAvailablePoint(80_000);

            // then
            assertAll(
                    () -> assertThat(member.getTotalPoint()).isEqualTo(100_000),
                    () -> assertThat(member.getAvailablePoint()).isEqualTo(100_000 - 80_000)
            );
        }
    }

    @Test
    @DisplayName("동일한 사용자인지 확인한다")
    void isSameMember() {
        // given
        final Member member = MEMBER_A.toMember().apply(1L);
        final Member other = MEMBER_B.toMember().apply(2L);

        // when
        final boolean actual1 = member.isSameMember(member);
        final boolean actual2 = member.isSameMember(other);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
