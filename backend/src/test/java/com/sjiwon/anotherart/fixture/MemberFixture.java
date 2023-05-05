package com.sjiwon.anotherart.fixture;

import com.sjiwon.anotherart.common.utils.PasswordEncoderUtils;
import com.sjiwon.anotherart.member.domain.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberFixture {
    MEMBER_A("사용자A", "사용자A", "user", "abcABC123!@#",
            "sjiwon4491@gmail.com", 12345, "기본 주소", "상세 주소"),
    MEMBER_B("사용자B", "사용자B", "admin", "abcABC123!@#",
            "sjiwon4491@naver.com", 12345, "기본 주소", "상세 주소"),
    MEMBER_C("사용자C", "사용자C", "manager", "abcABC123!@#",
            "sjiwon@kyonggi.ac.kr", 12345, "기본 주소", "상세 주소"),
    MEMBER_D("사용자D", "사용자D", "anonymous", "abcABC123!@#",
            "sjiwon4491@kakao.com", 12345, "기본 주소", "상세 주소"),
    ;

    private final String name;
    private final String nickname;
    private final String loginId;
    private final String password;
    private final String email;
    private final int postcode;
    private final String defaultAddress;
    private final String detailAddress;

    public Member toMember() {
        return Member.createMember(
                name,
                Nickname.from(nickname),
                loginId,
                Password.encrypt(password, PasswordEncoderUtils.getEncoder()),
                "경기대학교",
                generateRandomPhoneNumber(),
                Email.from(email),
                Address.of(postcode, defaultAddress, detailAddress)
        );
    }

    private static String generateRandomPhoneNumber() {
        String first = "010";
        String second = String.valueOf((int) (Math.random() * 9000 + 1000));
        String third = String.valueOf((int) (Math.random() * 9000 + 1000));

        return first + second + third;
    }
}
