package com.sjiwon.anotherart.member.application.usecase.query.response;

import com.sjiwon.anotherart.member.domain.repository.query.response.MemberInformation;

public record MemberInformationResponse(
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
    public static MemberInformationResponse from(final MemberInformation result) {
        return new MemberInformationResponse(
                result.id(),
                result.name(),
                result.nickname(),
                result.loginId(),
                result.school(),
                result.phone(),
                result.email(),
                result.postCode(),
                result.defaultAddress(),
                result.detailAddress(),
                result.totalPoint(),
                result.availablePoint()
        );
    }
}
