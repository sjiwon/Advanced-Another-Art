package com.sjiwon.anotherart.member.controller.utils;

import com.sjiwon.anotherart.member.controller.dto.request.SignUpRequest;
import com.sjiwon.anotherart.member.domain.Member;

public class SignUpRequestUtils {
    public static SignUpRequest createEmptyRequest() {
        return SignUpRequest.builder().build();
    }

    public static SignUpRequest createFailureSignUpRequest(Member member) {
        return SignUpRequest.builder()
                .name(member.getName())
                .nickname(member.getNickname())
                .loginId(member.getLoginId())
                .password("abcABC123!@#")
                .school(member.getSchool())
                .postcode(12345)
                .defaultAddress("기본 주소")
                .detailAddress("상세 주소")
                .phone(member.getPhone())
                .email(member.getEmail().getValue())
                .build();
    }

    public static SignUpRequest createSuccessSignUpRequest() {
        return SignUpRequest.builder()
                .name("test")
                .nickname("test")
                .loginId("test")
                .password("abcABC123!@#")
                .school("경기대학교")
                .postcode(12345)
                .defaultAddress("기본 주소")
                .detailAddress("상세 주소")
                .phone("01012345678")
                .email("test@gmail.com")
                .build();
    }
}
