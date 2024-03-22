package com.sjiwon.anotherart.member.domain.repository.query.response;

public record MemberInformation(
        Long id,
        String name,
        String nickname,
        String loginId,
        String school,
        String phone,
        String email,
        int postCode,
        String defaultAddress,
        String detailAddress,
        int totalPoint,
        int availablePoint
) {
}
