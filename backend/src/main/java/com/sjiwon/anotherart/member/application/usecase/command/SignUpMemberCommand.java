package com.sjiwon.anotherart.member.application.usecase.command;

import com.sjiwon.anotherart.member.domain.model.Address;
import com.sjiwon.anotherart.member.domain.model.Email;
import com.sjiwon.anotherart.member.domain.model.Nickname;
import com.sjiwon.anotherart.member.domain.model.Phone;

public record SignUpMemberCommand(
        String name,
        Nickname nickname,
        String loginId,
        String password,
        String school,
        Phone phone,
        Email email,
        Address address
) {
}
