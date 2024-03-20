package com.sjiwon.anotherart.member.application.usecase;

import com.sjiwon.anotherart.global.annotation.UseCase;
import com.sjiwon.anotherart.global.utils.encrypt.Encryptor;
import com.sjiwon.anotherart.member.application.usecase.command.SignUpMemberCommand;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.model.Password;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import com.sjiwon.anotherart.member.domain.service.MemberResourceValidator;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class SignUpMemberUseCase {
    private final MemberResourceValidator memberResourceValidator;
    private final Encryptor encryptor;
    private final MemberRepository memberRepository;

    public Long invoke(final SignUpMemberCommand command) {
        memberResourceValidator.validateInSignUp(command.loginId(), command.email(), command.nickname(), command.phone());

        final Member member = build(command);
        return memberRepository.save(member).getId();
    }

    private Member build(final SignUpMemberCommand command) {
        return Member.createMember(
                command.name(),
                command.nickname(),
                command.loginId(),
                Password.encrypt(command.password(), encryptor),
                command.school(),
                command.phone(),
                command.email(),
                command.address()
        );
    }
}
