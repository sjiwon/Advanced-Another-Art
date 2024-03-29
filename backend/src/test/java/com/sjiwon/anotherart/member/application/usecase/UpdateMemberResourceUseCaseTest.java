package com.sjiwon.anotherart.member.application.usecase;

import com.sjiwon.anotherart.common.UnitTest;
import com.sjiwon.anotherart.common.mock.fake.FakeEncryptor;
import com.sjiwon.anotherart.global.utils.encrypt.Encryptor;
import com.sjiwon.anotherart.member.application.usecase.command.UpdateAddressCommand;
import com.sjiwon.anotherart.member.application.usecase.command.UpdateNicknameCommand;
import com.sjiwon.anotherart.member.application.usecase.command.UpdatePasswordCommand;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.service.MemberReader;
import com.sjiwon.anotherart.member.exception.MemberException;
import com.sjiwon.anotherart.member.exception.MemberExceptionCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_B;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Member -> UpdateMemberResourceUseCase 테스트")
class UpdateMemberResourceUseCaseTest extends UnitTest {
    private final Encryptor encryptor = new FakeEncryptor();
    private final UpdateMemberResourceUseCase sut = new UpdateMemberResourceUseCase(
            new MemberReader(memberRepository),
            encryptor
    );

    private final Member member = MEMBER_A.toDomain().apply(1L);
    private final Member other = MEMBER_B.toDomain().apply(2L);

    @Nested
    @DisplayName("닉네임 수정")
    class UpdateNickname {

        @Test
        @DisplayName("타인이 사용하고 있는 닉네임으로 수정할 수 없다")
        void throwExceptionByNicknameIsInUseByOther() {
            // given
            final UpdateNicknameCommand command = new UpdateNicknameCommand(member.getId(), MEMBER_B.getNickname().getValue());
            given(memberRepository.findIdByNickname(command.nickname())).willReturn(other.getId());

            // when - then
            assertThatThrownBy(() -> sut.updateNickname(command))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(MemberExceptionCode.DUPLICATE_NICKNAME.getMessage());

            assertAll(
                    () -> verify(memberRepository, times(1)).findIdByNickname(command.nickname()),
                    () -> verify(memberRepository, times(0)).findById(command.memberId())
            );
        }

        @Test
        @DisplayName("이전과 동일한 닉네임으로 수정할 수 없다")
        void throwExceptionByNicknameSameAsBefore() {
            /// given
            final UpdateNicknameCommand command = new UpdateNicknameCommand(member.getId(), MEMBER_A.getNickname().getValue());
            given(memberRepository.findIdByNickname(command.nickname())).willReturn(member.getId());
            given(memberRepository.findById(command.memberId())).willReturn(Optional.of(member));

            // when - then
            assertThatThrownBy(() -> sut.updateNickname(command))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(MemberExceptionCode.NICKNAME_SAME_AS_BEFORE.getMessage());

            assertAll(
                    () -> verify(memberRepository, times(1)).findIdByNickname(command.nickname()),
                    () -> verify(memberRepository, times(1)).findById(command.memberId())
            );
        }

        @Test
        @DisplayName("사용자의 닉네임을 수정한다")
        void success() {
            // given
            final UpdateNicknameCommand command = new UpdateNicknameCommand(member.getId(), MEMBER_B.getNickname().getValue());
            given(memberRepository.findIdByNickname(command.nickname())).willReturn(null);
            given(memberRepository.findById(command.memberId())).willReturn(Optional.of(member));

            // when
            sut.updateNickname(command);

            // then
            assertAll(
                    () -> verify(memberRepository, times(1)).findIdByNickname(command.nickname()),
                    () -> verify(memberRepository, times(1)).findById(command.memberId()),
                    () -> assertThat(member.getNickname().getValue()).isEqualTo(command.nickname())
            );
        }
    }

    @Nested
    @DisplayName("주소 수정")
    class UpdateAddress {
        private final UpdateAddressCommand command = new UpdateAddressCommand(
                member.getId(),
                MEMBER_B.getAddress().getPostcode(),
                MEMBER_B.getAddress().getDefaultAddress(),
                MEMBER_B.getAddress().getDetailAddress()
        );

        @Test
        @DisplayName("사용자의 주소를 수정한다")
        void success() {
            // given
            given(memberRepository.findById(command.memberId())).willReturn(Optional.of(member));

            // when
            sut.updateAddress(command);

            // then
            assertAll(
                    () -> verify(memberRepository, times(1)).findById(command.memberId()),
                    () -> assertThat(member.getAddress().getPostcode()).isEqualTo(command.postcode()),
                    () -> assertThat(member.getAddress().getDefaultAddress()).isEqualTo(command.defaultAddress()),
                    () -> assertThat(member.getAddress().getDetailAddress()).isEqualTo(command.detailAddress())
            );
        }
    }

    @Nested
    @DisplayName("비밀번호 수정")
    class UpdatePassword {
        private final Encryptor encryptor = new FakeEncryptor();

        @Test
        @DisplayName("이전과 동일한 비밀번호로 수정할 수 없다")
        void throwExceptionByPasswordSameAsBefore() {
            // given
            final UpdatePasswordCommand command = new UpdatePasswordCommand(member.getId(), MEMBER_A.getPassword());
            given(memberRepository.findById(command.memberId())).willReturn(Optional.of(member));

            // when
            assertThatThrownBy(() -> sut.updatePassword(command))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(MemberExceptionCode.PASSWORD_SAME_AS_BEFORE.getMessage());

            verify(memberRepository, times(1)).findById(command.memberId());
        }

        @Test
        @DisplayName("사용자의 비밀번호를 수정한다")
        void success() {
            // given
            final UpdatePasswordCommand command = new UpdatePasswordCommand(member.getId(), MEMBER_B.getPassword());
            given(memberRepository.findById(command.memberId())).willReturn(Optional.of(member));

            // when
            sut.updatePassword(command);

            // then
            assertAll(
                    () -> verify(memberRepository, times(1)).findById(command.memberId()),
                    () -> assertThat(encryptor.matches(MEMBER_B.getPassword(), member.getPassword().getValue())).isTrue(),
                    () -> assertThat(encryptor.matches(MEMBER_A.getPassword(), member.getPassword().getValue())).isFalse()
            );
        }
    }
}
