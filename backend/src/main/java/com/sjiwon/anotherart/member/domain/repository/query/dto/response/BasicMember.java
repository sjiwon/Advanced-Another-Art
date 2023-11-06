package com.sjiwon.anotherart.member.domain.repository.query.dto.response;

import com.sjiwon.anotherart.member.domain.model.Nickname;

public record BasicMember(
        Long id,
        String nickname,
        String school
) {
    public BasicMember(final Long id, final Nickname nickname, final String school) {
        this(id, nickname.getValue(), school);
    }
}
