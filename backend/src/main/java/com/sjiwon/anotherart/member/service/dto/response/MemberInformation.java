package com.sjiwon.anotherart.member.service.dto.response;

import com.sjiwon.anotherart.member.domain.Address;
import com.sjiwon.anotherart.member.domain.Member;

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
    public MemberInformation(Member member) {
        this(
                member.getId(),
                member.getName(),
                member.getNicknameValue(),
                member.getLoginId(),
                member.getSchool(),
                member.getPhone(),
                member.getEmailValue(),
                member.getAddress(),
                member.getTotalPoint(),
                member.getAvailablePoint()
        );
    }
}
