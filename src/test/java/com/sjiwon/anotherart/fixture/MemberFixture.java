package com.sjiwon.anotherart.fixture;

import com.sjiwon.anotherart.member.domain.Address;
import com.sjiwon.anotherart.member.domain.Email;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.Password;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public enum MemberFixture {
    A("A", "A", "user1", "abcAbc123!@#", 12345, "서울", "토스"),
    B("B", "B", "user2", "abcAbc123!@#", 12345, "서울", "토스"),
    C("C", "C", "user3", "abcAbc123!@#", 12345, "서울", "토스"),
    ;

    private final String name;
    private final String nickname;
    private final String loginId;
    private final String password;
    private final int postcode;
    private final String defaultAddress;
    private final String detailAddress;

    public Member toMember(PasswordEncoder encoder) {
        return Member.builder()
                .name(name)
                .nickname(nickname)
                .loginId(loginId)
                .password(Password.encrypt(password, encoder))
                .school("경기대학교")
                .address(Address.of(postcode, defaultAddress, detailAddress))
                .phone(generateRandomPhoneNumber())
                .email(Email.from(generateRandomEmail()))
                .build();
    }

    private static String generateRandomEmail() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10) + "@gmail.com";
    }

    private static String generateRandomPhoneNumber() {
        String result = "010";
        result += String.valueOf((int) (Math.random() * 9000 + 1000));
        result += String.valueOf((int) (Math.random() * 9000 + 1000));
        return result;
    }
}
