package com.sjiwon.anotherart.member.controller.utils;

import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.member.controller.dto.request.SignUpRequest;

import java.util.UUID;

public class SignUpRequestUtils {
    public static SignUpRequest createEmptyRequest() {
        return SignUpRequest.builder()
                .name(null)
                .nickname(null)
                .loginId(null)
                .password(null)
                .school(null)
                .postcode(null)
                .defaultAddress(null)
                .detailAddress(null)
                .phone(null)
                .email(null)
                .build();
    }

    public static SignUpRequest createFailureSignUpRequest() {
        return SignUpRequest.builder()
                .name(MemberFixture.A.getName())
                .nickname(MemberFixture.A.getNickname())
                .loginId(MemberFixture.A.getLoginId())
                .password(MemberFixture.A.getPassword())
                .school("경기대학교")
                .postcode(MemberFixture.A.getPostcode())
                .defaultAddress(MemberFixture.A.getDefaultAddress())
                .detailAddress(MemberFixture.A.getDetailAddress())
                .phone(generateRandomPhoneNumber())
                .email(generateRandomEmail())
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
                .phone(generateRandomPhoneNumber())
                .email(generateRandomEmail())
                .build();
    }

    private static String generateRandomPhoneNumber() {
        String result = "010";
        result += String.valueOf((int) (Math.random() * 9000 + 1000));
        result += String.valueOf((int) (Math.random() * 9000 + 1000));
        return result;
    }

    private static String generateRandomEmail() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10) + "@gmail.com";
    }
}
