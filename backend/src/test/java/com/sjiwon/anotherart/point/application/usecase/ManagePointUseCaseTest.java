package com.sjiwon.anotherart.point.application.usecase;

import com.sjiwon.anotherart.common.UnitTest;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.service.MemberReader;
import com.sjiwon.anotherart.member.exception.MemberException;
import com.sjiwon.anotherart.member.exception.MemberExceptionCode;
import com.sjiwon.anotherart.point.application.usecase.command.ChargePointCommand;
import com.sjiwon.anotherart.point.application.usecase.command.RefundPointCommand;
import com.sjiwon.anotherart.point.domain.model.PointRecord;
import com.sjiwon.anotherart.point.domain.service.PointRecordWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Point -> ManagePointUseCase 테스트")
class ManagePointUseCaseTest extends UnitTest {
    private final ManagePointUseCase sut = new ManagePointUseCase(
            new MemberReader(memberRepository),
            new PointRecordWriter(pointRecordRepository)
    );

    @Nested
    @DisplayName("포인트 충전")
    class Charge {
        private Member member;
        private ChargePointCommand command;

        @BeforeEach
        void setUp() {
            member = MEMBER_A.toDomain().apply(1L);
            command = new ChargePointCommand(member.getId(), 100_000);
        }

        @Test
        @DisplayName("포인트를 충전한다")
        void success() {
            // given
            given(memberRepository.findById(command.memberId())).willReturn(Optional.of(member));

            // when
            sut.charge(command);

            // then
            assertAll(
                    () -> verify(memberRepository, times(1)).findById(command.memberId()),
                    () -> verify(pointRecordRepository, times(1)).save(any(PointRecord.class)),
                    () -> assertThat(member.getTotalPoint()).isEqualTo(command.chargeAmount()),
                    () -> assertThat(member.getAvailablePoint()).isEqualTo(command.chargeAmount())
            );
        }
    }

    @Nested
    @DisplayName("포인트 환불")
    class Refund {
        private Member member;
        private RefundPointCommand command;

        @BeforeEach
        void setUp() {
            member = MEMBER_A.toDomain().apply(1L);
            command = new RefundPointCommand(member.getId(), 50_000);
        }

        @Test
        @DisplayName("사용 가능한 포인트가 충분하지 않음에 따라 환불을 진행할 수 없다")
        void throwExceptionByPointIsNotEnough() {
            // given
            given(memberRepository.findById(command.memberId())).willReturn(Optional.of(member));

            // when - then
            assertThatThrownBy(() -> sut.refund(command))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(MemberExceptionCode.POINT_IS_NOT_ENOUGH.getMessage());

            assertAll(
                    () -> verify(memberRepository, times(1)).findById(command.memberId()),
                    () -> verify(pointRecordRepository, times(0)).save(any(PointRecord.class))
            );
        }

        @Test
        @DisplayName("포인트를 환불한다")
        void success() {
            // given
            final int INIT_POINT = 100_000;
            member.increaseTotalPoint(INIT_POINT);
            given(memberRepository.findById(command.memberId())).willReturn(Optional.of(member));

            // when
            sut.refund(command);

            // then
            assertAll(
                    () -> verify(memberRepository, times(1)).findById(command.memberId()),
                    () -> verify(pointRecordRepository, times(1)).save(any(PointRecord.class)),
                    () -> assertThat(member.getTotalPoint()).isEqualTo(INIT_POINT - command.refundAmount()),
                    () -> assertThat(member.getAvailablePoint()).isEqualTo(INIT_POINT - command.refundAmount())
            );
        }
    }
}
