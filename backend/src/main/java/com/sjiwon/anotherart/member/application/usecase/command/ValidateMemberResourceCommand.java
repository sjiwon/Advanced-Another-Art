package com.sjiwon.anotherart.member.application.usecase.command;

import com.sjiwon.anotherart.member.domain.model.MemberDuplicateResource;

public record ValidateMemberResourceCommand(
        MemberDuplicateResource resource,
        String value
) {
}
