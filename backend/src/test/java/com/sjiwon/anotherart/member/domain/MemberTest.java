package com.sjiwon.anotherart.member.domain;

import com.sjiwon.anotherart.common.utils.PasswordEncoderUtils;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.point.PointRecord;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_B;
import static com.sjiwon.anotherart.member.domain.Role.USER;
import static com.sjiwon.anotherart.member.domain.point.PointType.CHARGE;
import static com.sjiwon.anotherart.member.domain.point.PointType.PURCHASE;
import static com.sjiwon.anotherart.member.domain.point.PointType.REFUND;
import static com.sjiwon.anotherart.member.domain.point.PointType.SOLD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member 도메인 테스트")
class MemberTest {
    private static final PasswordEncoder ENCODER = PasswordEncoderUtils.getEncoder();

    @Test
    @DisplayName("멤버를 생성한다")
    void construct() {
        final Member member = MEMBER_A.toMember();

        assertAll(
                () -> assertThat(member.getName()).isEqualTo(MEMBER_A.getName()),
                () -> assertThat(member.getNicknameValue()).isEqualTo(MEMBER_A.getNickname()),
                () -> assertThat(member.getLoginId()).isEqualTo(MEMBER_A.getLoginId()),
                () -> assertThat(ENCODER.matches(MEMBER_A.getPassword(), member.getPasswordValue())).isTrue(),
                () -> assertThat(member.getEmailValue()).isEqualTo(MEMBER_A.getEmail()),
                () -> assertThat(member.getAddress().getPostcode()).isEqualTo(MEMBER_A.getPostcode()),
                () -> assertThat(member.getAddress().getDefaultAddress()).isEqualTo(MEMBER_A.getDefaultAddress()),
                () -> assertThat(member.getAddress().getDetailAddress()).isEqualTo(MEMBER_A.getDetailAddress()),
                () -> assertThat(member.getTotalPoint()).isEqualTo(0),
                () -> assertThat(member.getAvailablePoint()).isEqualTo(0),
                () -> assertThat(member.getRole()).isEqualTo(USER)
        );
    }

    @Nested
    @DisplayName("닉네임 변경")
    class changeNickname {
        private Member member;

        @BeforeEach
        void setUp() {
            member = MEMBER_A.toMember();
        }

        @Test
        @DisplayName("이전과 동일한 닉네임으로 변경할 수 없다")
        void throwExceptionByNicknameSameAsBefore() {
            assertThatThrownBy(() -> member.changeNickname(member.getNicknameValue()))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(MemberErrorCode.NICKNAME_SAME_AS_BEFORE.getMessage());
        }

        @Test
        @DisplayName("닉네임 변경에 성공한다")
        void success() {
            // when
            final String changeNickname = "Messi";
            member.changeNickname(changeNickname);

            // then
            assertThat(member.getNicknameValue()).isEqualTo(changeNickname);
        }
    }

    @Nested
    @DisplayName("비밀번호 변경")
    class changePassword {
        private Member member;

        @BeforeEach
        void setUp() {
            member = MEMBER_A.toMember();
        }

        @Test
        @DisplayName("이전과 동일한 비밀번호호 변경할 수 없다")
        void throwExceptionByPasswordSameAsBefore() {
            assertThatThrownBy(() -> member.changePassword(MEMBER_A.getPassword(), ENCODER))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(MemberErrorCode.PASSWORD_SAME_AS_BEFORE.getMessage());
        }

        @Test
        @DisplayName("비밀번호 변경에 성공한다")
        void success() {
            // when
            final String changePassword = "asd!@#032sda02#";
            member.changePassword(changePassword, ENCODER);

            // then
            assertThat(ENCODER.matches("asd!@#032sda02#", member.getPasswordValue())).isTrue();
        }
    }

    @Test
    @DisplayName("주소를 변경한다")
    void changeAddress() {
        // given
        final Member member = MEMBER_A.toMember();

        // when
        final int changePostcode = 98765;
        final String changeDefault = "성남";
        final String changeDetail = "카카오";
        member.changeAddress(changePostcode, changeDefault, changeDetail);

        // then
        assertAll(
                () -> assertThat(member.getAddress().getPostcode()).isEqualTo(changePostcode),
                () -> assertThat(member.getAddress().getDefaultAddress()).isEqualTo(changeDefault),
                () -> assertThat(member.getAddress().getDetailAddress()).isEqualTo(changeDetail)
        );
    }

    @Nested
    @DisplayName("동일 사용자 검증")
    class validSameMember {
        private Member member;
        private Member other;

        @BeforeEach
        void setUp() {
            member = MEMBER_A.toMember().apply(1L);
            other = MEMBER_B.toMember().apply(2L);
        }

        @Test
        @DisplayName("ID(PK)를 통해서 동일 사용자인지 검증한다")
        void isSameMember() {
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

    @Test
    @DisplayName("포인트 활용 내역을 기록한다")
    void addPointRecords() {
        // given
        final Member member = MEMBER_A.toMember();
        final int INCREASE_AMOUNT = 100_000;
        final int DECREASE_AMOUNT = 50_000;

        /* 포인트 충전 */
        member.addPointRecords(CHARGE, INCREASE_AMOUNT);
        assertAll(
                () -> assertThat(member.getPointRecords()).hasSize(1),
                () -> assertThat(member.getPointRecords())
                        .map(PointRecord::getType)
                        .containsExactlyInAnyOrder(CHARGE),
                () -> assertThat(member.getTotalPoint()).isEqualTo(INCREASE_AMOUNT),
                () -> assertThat(member.getAvailablePoint()).isEqualTo(INCREASE_AMOUNT)
        );

        /* 포인트 환불 */
        member.addPointRecords(REFUND, DECREASE_AMOUNT);
        assertAll(
                () -> assertThat(member.getPointRecords()).hasSize(2),
                () -> assertThat(member.getPointRecords())
                        .map(PointRecord::getType)
                        .containsExactlyInAnyOrder(CHARGE, REFUND),
                () -> assertThat(member.getTotalPoint()).isEqualTo(INCREASE_AMOUNT - DECREASE_AMOUNT),
                () -> assertThat(member.getAvailablePoint()).isEqualTo(INCREASE_AMOUNT - DECREASE_AMOUNT)
        );

        /* 작품 구매 */
        member.addPointRecords(PURCHASE, DECREASE_AMOUNT);
        assertAll(
                () -> assertThat(member.getPointRecords()).hasSize(3),
                () -> assertThat(member.getPointRecords())
                        .map(PointRecord::getType)
                        .containsExactlyInAnyOrder(CHARGE, REFUND, PURCHASE),
                () -> assertThat(member.getTotalPoint())
                        .isEqualTo(INCREASE_AMOUNT - DECREASE_AMOUNT - DECREASE_AMOUNT),
                () -> assertThat(member.getAvailablePoint())
                        .isEqualTo(INCREASE_AMOUNT - DECREASE_AMOUNT - DECREASE_AMOUNT)
        );

        /* 작품 판매 */
        member.addPointRecords(SOLD, INCREASE_AMOUNT);
        assertAll(
                () -> assertThat(member.getPointRecords()).hasSize(4),
                () -> assertThat(member.getPointRecords())
                        .map(PointRecord::getType)
                        .containsExactlyInAnyOrder(CHARGE, REFUND, PURCHASE, SOLD),
                () -> assertThat(member.getTotalPoint())
                        .isEqualTo(INCREASE_AMOUNT - DECREASE_AMOUNT - DECREASE_AMOUNT + INCREASE_AMOUNT),
                () -> assertThat(member.getAvailablePoint())
                        .isEqualTo(INCREASE_AMOUNT - DECREASE_AMOUNT - DECREASE_AMOUNT + INCREASE_AMOUNT)
        );
    }

    @Nested
    @DisplayName("경매 작품 입찰 관련 사용 가능한 포인트")
    class aboutAuctionBid {
        @Test
        @DisplayName("경매 작품에 입찰을 진행함에 따라 입찰가만큼 사용 가능한 포인트가 감소한다")
        void becomeTopBidder() {
            // given
            final Member member = MEMBER_A.toMember();
            member.addPointRecords(CHARGE, 100_000);

            // when
            member.decreaseAvailablePoint(30_000);

            // then
            assertAll(
                    () -> assertThat(member.getTotalPoint()).isEqualTo(100_000),
                    () -> assertThat(member.getAvailablePoint()).isEqualTo(100_000 - 30_000)
            );
        }

        @Test
        @DisplayName("최고 입찰자에서 물러남에 따라 이전에 입찰한 금액을 다시 사용 가능한 포인트에 누적시킨다")
        void withdrawFromAuctionAsTopBidder() {
            // given
            final Member member = MEMBER_A.toMember();
            member.addPointRecords(CHARGE, 100_000);

            // when
            member.increaseAvailablePoint(30_000);

            // then
            assertAll(
                    () -> assertThat(member.getTotalPoint()).isEqualTo(100_000),
                    () -> assertThat(member.getAvailablePoint()).isEqualTo(100_000 + 30_000)
            );
        }
    }
}
