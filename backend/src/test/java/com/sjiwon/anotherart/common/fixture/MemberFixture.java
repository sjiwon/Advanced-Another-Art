package com.sjiwon.anotherart.common.fixture;

import com.sjiwon.anotherart.common.mock.fake.FakeEncryptor;
import com.sjiwon.anotherart.member.domain.model.Address;
import com.sjiwon.anotherart.member.domain.model.Email;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.model.Nickname;
import com.sjiwon.anotherart.member.domain.model.Password;
import com.sjiwon.anotherart.member.domain.model.Phone;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberFixture {
    MEMBER_A(
            "사용자A", Nickname.from("사용자A"),
            "user", "userabcABC123!@#",
            Phone.from("010-1111-1234"), Email.from("sjiwon4491@gmail.com"),
            Address.of(12345, "기본 주소 user", "상세 주소 user")
    ),
    MEMBER_B(
            "사용자B", Nickname.from("사용자B"),
            "admin", "adminabcABC123!@#",
            Phone.from("010-1111-5678"), Email.from("sjiwon4491@naver.com"),
            Address.of(12345, "기본 주소 admin", "상세 주소 admin")
    ),
    MEMBER_C(
            "사용자C", Nickname.from("사용자C"),
            "manager", "managerabcABC123!@#",
            Phone.from("010-1111-9012"), Email.from("sjiwon@kyonggi.ac.kr"),
            Address.of(12345, "기본 주소 manager", "상세 주소 manager")
    ),
    MEMBER_D(
            "사용자D", Nickname.from("사용자D"),
            "anonymous", "anonymousabcABC123!@#",
            Phone.from("010-1111-3456"), Email.from("sjiwon4491@kakao.com"),
            Address.of(12345, "기본 주소 anonymous", "상세 주소 anonymous")
    ),

    DUMMY_1(
            "더미1", Nickname.from("더미1"),
            "dummy1", "dummy1abcABC123!@#",
            Phone.from("010-1111-0001"), Email.from("dummy1@gmail.com"),
            Address.of(12345, "기본 주소 dummy1", "상세 주소 dummy1")
    ),
    DUMMY_2(
            "더미2", Nickname.from("더미2"),
            "dummy2", "dummy2abcABC123!@#",
            Phone.from("010-1111-0002"), Email.from("dummy2@gmail.com"),
            Address.of(12345, "기본 주소 dummy2", "상세 주소 dummy2")
    ),
    DUMMY_3(
            "더미3", Nickname.from("더미3"),
            "dummy3", "dummy3abcABC123!@#",
            Phone.from("010-1111-0003"), Email.from("dummy3@gmail.com"),
            Address.of(12345, "기본 주소 dummy3", "상세 주소 dummy3")
    ),
    DUMMY_4(
            "더미4", Nickname.from("더미4"),
            "dummy4", "dummy4abcABC123!@#",
            Phone.from("010-1111-0004"), Email.from("dummy4@gmail.com"),
            Address.of(12345, "기본 주소 dummy4", "상세 주소 dummy4")
    ),
    DUMMY_5(
            "더미5", Nickname.from("더미5"),
            "dummy5", "dummy5abcABC123!@#",
            Phone.from("010-1111-0005"), Email.from("dummy5@gmail.com"),
            Address.of(12345, "기본 주소 dummy5", "상세 주소 dummy5")
    ),
    DUMMY_6(
            "더미6", Nickname.from("더미6"),
            "dummy6", "dummy6abcABC123!@#",
            Phone.from("010-1111-0006"), Email.from("dummy6@gmail.com"),
            Address.of(12345, "기본 주소 dummy6", "상세 주소 dummy6")
    ),
    DUMMY_7(
            "더미7", Nickname.from("더미7"),
            "dummy7", "dummy7abcABC123!@#",
            Phone.from("010-1111-0007"), Email.from("dummy7@gmail.com"),
            Address.of(12345, "기본 주소 dummy7", "상세 주소 dummy7")
    ),
    DUMMY_8(
            "더미8", Nickname.from("더미8"),
            "dummy8", "dummy8abcABC123!@#",
            Phone.from("010-1111-0008"), Email.from("dummy8@gmail.com"),
            Address.of(12345, "기본 주소 dummy8", "상세 주소 dummy8")
    ),
    DUMMY_9(
            "더미9", Nickname.from("더미9"),
            "dummy9", "dummy9abcABC123!@#",
            Phone.from("010-1111-0009"), Email.from("dummy9@gmail.com"),
            Address.of(12345, "기본 주소 dummy9", "상세 주소 dummy9")
    ),
    DUMMY_10(
            "더미10", Nickname.from("더미10"),
            "dummy10", "dummy10abcABC123!@#",
            Phone.from("010-1111-0010"), Email.from("dummy10@gmail.com"),
            Address.of(12345, "기본 주소 dummy10", "상세 주소 dummy10")
    ),
    DUMMY_11(
            "더미11", Nickname.from("더미11"),
            "dummy11", "dummy11abcABC123!@#",
            Phone.from("010-1111-0011"), Email.from("dummy11@gmail.com"),
            Address.of(12345, "기본 주소 dummy11", "상세 주소 dummy11")
    ),
    DUMMY_12(
            "더미12", Nickname.from("더미12"),
            "dummy12", "dummy12abcABC123!@#",
            Phone.from("010-1111-0012"), Email.from("dummy12@gmail.com"),
            Address.of(12345, "기본 주소 dummy12", "상세 주소 dummy12")
    ),
    DUMMY_13(
            "더미13", Nickname.from("더미13"),
            "dummy13", "dummy13abcABC123!@#",
            Phone.from("010-1111-0013"), Email.from("dummy13@gmail.com"),
            Address.of(12345, "기본 주소 dummy13", "상세 주소 dummy13")
    ),
    DUMMY_14(
            "더미14", Nickname.from("더미14"),
            "dummy14", "dummy14abcABC123!@#",
            Phone.from("010-1111-0014"), Email.from("dummy14@gmail.com"),
            Address.of(12345, "기본 주소 dummy14", "상세 주소 dummy14")
    ),
    DUMMY_15(
            "더미15", Nickname.from("더미15"),
            "dummy15", "dummy15abcABC123!@#",
            Phone.from("010-1111-0015"), Email.from("dummy15@gmail.com"),
            Address.of(12345, "기본 주소 dummy15", "상세 주소 dummy15")
    ),
    DUMMY_16(
            "더미16", Nickname.from("더미16"),
            "dummy16", "dummy16abcABC123!@#",
            Phone.from("010-1111-0016"), Email.from("dummy16@gmail.com"),
            Address.of(12345, "기본 주소 dummy16", "상세 주소 dummy16")
    ),
    DUMMY_17(
            "더미17", Nickname.from("더미17"),
            "dummy17", "dummy17abcABC123!@#",
            Phone.from("010-1111-0017"), Email.from("dummy17@gmail.com"),
            Address.of(12345, "기본 주소 dummy17", "상세 주소 dummy17")
    ),
    DUMMY_18(
            "더미18", Nickname.from("더미18"),
            "dummy18", "dummy18abcABC123!@#",
            Phone.from("010-1111-0018"), Email.from("dummy18@gmail.com"),
            Address.of(12345, "기본 주소 dummy18", "상세 주소 dummy18")
    ),
    DUMMY_19(
            "더미19", Nickname.from("더미19"),
            "dummy19", "dummy19abcABC123!@#",
            Phone.from("010-1111-0019"), Email.from("dummy19@gmail.com"),
            Address.of(12345, "기본 주소 dummy19", "상세 주소 dummy19")
    ),
    DUMMY_20(
            "더미20", Nickname.from("더미20"),
            "dummy20", "dummy20abcABC123!@#",
            Phone.from("010-1111-0020"), Email.from("dummy20@gmail.com"),
            Address.of(12345, "기본 주소 dummy20", "상세 주소 dummy20")
    ),
    ;

    private final String name;
    private final Nickname nickname;
    private final String loginId;
    private final String password;
    private final Phone phone;
    private final Email email;
    private final Address address;

    public Member toMember() {
        return Member.createMember(
                name,
                nickname,
                loginId,
                Password.encrypt(password, new FakeEncryptor()),
                "경기대학교",
                phone,
                email,
                address
        );
    }
}
