package com.sjiwon.anotherart.common.fixture;

import com.sjiwon.anotherart.common.mock.fake.FakePasswordEncryptor;
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
            "사용자A", "사용자A",
            "user", "abcABC123!@#", "01011111234",
            "sjiwon4491@gmail.com", 12345, "기본 주소", "상세 주소"
    ),
    MEMBER_B(
            "사용자B", "사용자B",
            "admin", "abcABC123!@#", "01011115678",
            "sjiwon4491@naver.com", 12345, "기본 주소", "상세 주소"
    ),
    MEMBER_C(
            "사용자C", "사용자C",
            "manager", "abcABC123!@#", "01011119012",
            "sjiwon@kyonggi.ac.kr", 12345, "기본 주소", "상세 주소"
    ),
    MEMBER_D(
            "사용자D", "사용자D",
            "anonymous", "abcABC123!@#", "01011113456",
            "sjiwon4491@kakao.com", 12345, "기본 주소", "상세 주소"
    ),

    DUMMY_1(
            "더미1", "더미1",
            "dummy1", "abcABC123!@#", "01011110001",
            "dummy1@gmail.com", 12345, "기본 주소", "상세 주소"
    ),
    DUMMY_2(
            "더미2", "더미2",
            "dummy2", "abcABC123!@#", "01011110002",
            "dummy2@gmail.com", 12345, "기본 주소", "상세 주소"
    ),
    DUMMY_3(
            "더미3", "더미3",
            "dummy3", "abcABC123!@#", "01011110003",
            "dummy3@gmail.com", 12345, "기본 주소", "상세 주소"
    ),
    DUMMY_4(
            "더미4", "더미4",
            "dummy4", "abcABC123!@#", "01011110004",
            "dummy4@gmail.com", 12345, "기본 주소", "상세 주소"
    ),
    DUMMY_5(
            "더미5", "더미5",
            "dummy5", "abcABC123!@#", "01011110005",
            "dummy5@gmail.com", 12345, "기본 주소", "상세 주소"
    ),
    DUMMY_6(
            "더미6", "더미6",
            "dummy6", "abcABC123!@#", "01011110006",
            "dummy6@gmail.com", 12345, "기본 주소", "상세 주소"
    ),
    DUMMY_7(
            "더미7", "더미7",
            "dummy7", "abcABC123!@#", "01011110007",
            "dummy7@gmail.com", 12345, "기본 주소", "상세 주소"
    ),
    DUMMY_8(
            "더미8", "더미8",
            "dummy8", "abcABC123!@#", "01011110008",
            "dummy8@gmail.com", 12345, "기본 주소", "상세 주소"
    ),
    DUMMY_9(
            "더미9", "더미9",
            "dummy9", "abcABC123!@#", "01011110009",
            "dummy9@gmail.com", 12345, "기본 주소", "상세 주소"
    ),
    DUMMY_10(
            "더미10", "더미10",
            "dummy10", "abcABC123!@#", "01011110010",
            "dummy10@gmail.com", 12345, "기본 주소", "상세 주소"
    ),
    DUMMY_11(
            "더미11", "더미11",
            "dummy11", "abcABC123!@#", "01011110011",
            "dummy11@gmail.com", 12345, "기본 주소", "상세 주소"
    ),
    DUMMY_12(
            "더미12", "더미12",
            "dummy12", "abcABC123!@#", "01011110012",
            "dummy12@gmail.com", 12345, "기본 주소", "상세 주소"
    ),
    DUMMY_13(
            "더미13", "더미13",
            "dummy13", "abcABC123!@#", "01011110013",
            "dummy13@gmail.com", 12345, "기본 주소", "상세 주소"
    ),
    DUMMY_14(
            "더미14", "더미14",
            "dummy14", "abcABC123!@#", "01011110014",
            "dummy14@gmail.com", 12345, "기본 주소", "상세 주소"
    ),
    DUMMY_15(
            "더미15", "더미15",
            "dummy15", "abcABC123!@#", "01011110015",
            "dummy15@gmail.com", 12345, "기본 주소", "상세 주소"
    ),
    DUMMY_16(
            "더미16", "더미16",
            "dummy16", "abcABC123!@#", "01011110016",
            "dummy16@gmail.com", 12345, "기본 주소", "상세 주소"
    ),
    DUMMY_17(
            "더미17", "더미17",
            "dummy17", "abcABC123!@#", "01011110017",
            "dummy17@gmail.com", 12345, "기본 주소", "상세 주소"
    ),
    DUMMY_18(
            "더미18", "더미18",
            "dummy18", "abcABC123!@#", "01011110018",
            "dummy18@gmail.com", 12345, "기본 주소", "상세 주소"
    ),
    DUMMY_19(
            "더미19", "더미19",
            "dummy19", "abcABC123!@#", "01011110019",
            "dummy19@gmail.com", 12345, "기본 주소", "상세 주소"
    ),
    DUMMY_20(
            "더미20", "더미20",
            "dummy20", "abcABC123!@#", "01011110020",
            "dummy20@gmail.com", 12345, "기본 주소", "상세 주소"
    ),
    ;

    private final String name;
    private final String nickname;
    private final String loginId;
    private final String password;
    private final String phone;
    private final String email;
    private final int postcode;
    private final String defaultAddress;
    private final String detailAddress;

    public Member toMember() {
        return Member.createMember(
                name,
                Nickname.from(nickname),
                loginId,
                Password.encrypt(password, new FakePasswordEncryptor()),
                "경기대학교",
                Phone.from(phone),
                Email.from(email),
                Address.of(postcode, defaultAddress, detailAddress)
        );
    }
}
