package com.sjiwon.anotherart.member.application.usecase;

import com.sjiwon.anotherart.common.UseCaseTest;
import com.sjiwon.anotherart.common.mock.fake.FakePasswordEncryptor;
import com.sjiwon.anotherart.global.encrypt.PasswordEncryptor;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.application.usecase.command.UpdateAddressCommand;
import com.sjiwon.anotherart.member.application.usecase.command.UpdateNicknameCommand;
import com.sjiwon.anotherart.member.application.usecase.command.UpdatePasswordCommand;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import com.sjiwon.anotherart.member.domain.service.MemberResourceValidator;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_B;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Member -> UpdateMemberResourceUseCase 테스트")
public class UpdateMemberResourceUseCaseTest extends UseCaseTest {
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final PasswordEncryptor passwordEncryptor = new FakePasswordEncryptor();
    private final MemberResourceValidator memberResourceValidator = new MemberResourceValidator(memberRepository);
    private final UpdateMemberResourceUseCase sut = new UpdateMemberResourceUseCase(memberResourceValidator, passwordEncryptor, memberRepository);

    private final Member member = MEMBER_A.toMember().apply(1L);

    @Nested
    @DisplayName("닉네임 수정")
    class UpdateNickname {

        @Test
        @DisplayName("타인이 사용하고 있는 닉네임으로 수정할 수 없다")
        void throwExceptionByNicknameIsInUseByOther() {
            // given
            final UpdateNicknameCommand command = new UpdateNicknameCommand(member.getId(), MEMBER_B.getNickname().getValue());
            given(memberRepository.isNicknameUsedByOther(command.memberId(), command.nickname())).willReturn(true);

            // when - then
            assertThatThrownBy(() -> sut.updateNickname(command))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(MemberErrorCode.DUPLICATE_NICKNAME.getMessage());

            assertAll(
                    () -> verify(memberRepository, times(1)).isNicknameUsedByOther(command.memberId(), command.nickname()),
                    () -> verify(memberRepository, times(0)).getById(command.memberId())
            );
        }

        @Test
        @DisplayName("이전과 동일한 닉네임으로 수정할 수 없다")
        void throwExceptionByNicknameSameAsBefore() {
            /// given
            final UpdateNicknameCommand command = new UpdateNicknameCommand(member.getId(), member.getNickname().getValue());
            given(memberRepository.isNicknameUsedByOther(command.memberId(), command.nickname())).willReturn(false);
            given(memberRepository.getById(command.memberId())).willReturn(member);

            // when - then
            assertThatThrownBy(() -> sut.updateNickname(command))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(MemberErrorCode.NICKNAME_SAME_AS_BEFORE.getMessage());

            assertAll(
                    () -> verify(memberRepository, times(1)).isNicknameUsedByOther(command.memberId(), command.nickname()),
                    () -> verify(memberRepository, times(1)).getById(command.memberId())
            );
        }

        @Test
        @DisplayName("사용자의 닉네임을 수정한다")
        void success() {
            // given
            final UpdateNicknameCommand command = new UpdateNicknameCommand(member.getId(), MEMBER_B.getNickname().getValue());
            given(memberRepository.isNicknameUsedByOther(command.memberId(), command.nickname())).willReturn(false);
            given(memberRepository.getById(command.memberId())).willReturn(member);

            // when
            sut.updateNickname(command);

            // then
            assertAll(
                    () -> verify(memberRepository, times(1)).isNicknameUsedByOther(command.memberId(), command.nickname()),
                    () -> verify(memberRepository, times(1)).getById(command.memberId()),
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
            given(memberRepository.getById(command.memberId())).willReturn(member);

            // when
            sut.updateAddress(command);

            // then
            assertAll(
                    () -> verify(memberRepository, times(1)).getById(command.memberId()),
                    () -> assertThat(member.getAddress().getPostcode()).isEqualTo(command.postcode()),
                    () -> assertThat(member.getAddress().getDefaultAddress()).isEqualTo(command.defaultAddress()),
                    () -> assertThat(member.getAddress().getDetailAddress()).isEqualTo(command.detailAddress())
            );
        }
    }

    @Nested
    @DisplayName("비밀번호 수정")
    class UpdatePassword {
        private final PasswordEncryptor passwordEncryptor = new FakePasswordEncryptor();
        private final UpdatePasswordCommand command = new UpdatePasswordCommand(
                member.getId(),
                MEMBER_B.getPassword()
        );

        @Test
        @DisplayName("사용자의 비밀번호를 수정한다")
        void success() {
            // given
            given(memberRepository.getById(command.memberId())).willReturn(member);

            // when
            sut.updatePassword(command);

            // then
            assertAll(
                    () -> verify(memberRepository, times(1)).getById(command.memberId()),
                    () -> assertThat(passwordEncryptor.matches(MEMBER_B.getPassword(), member.getPassword().getValue())).isTrue(),
                    () -> assertThat(passwordEncryptor.matches(MEMBER_A.getPassword(), member.getPassword().getValue())).isFalse()
            );
        }
    }
}
