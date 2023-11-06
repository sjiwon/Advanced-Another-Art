package com.sjiwon.anotherart.member.application.dto.response;

import com.sjiwon.anotherart.member.domain.model.Address;
import com.sjiwon.anotherart.member.domain.model.Member;

public record MemberInformation(
        Long id,
        String name,
        String nickname,
        String loginId,
        String school,
        String phone,
        String email,
        Address address,
        int totalPoint,
        int availablePoint
) {
    public MemberInformation(final Member member) {
        this(
                member.getId(),
                member.getName(),
                member.getNickname().getValue(),
                member.getLoginId(),
                member.getSchool(),
                member.getPhone().getValue(),
                member.getEmail().getValue(),
                member.getAddress(),
                member.getTotalPoint(),
                member.getAvailablePoint()
        );
    }
}
