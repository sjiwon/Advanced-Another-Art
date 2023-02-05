package com.sjiwon.anotherart.member.exception;

public class MemberRequestValidationMessage {
    public static class SignUp {
        public static final String MEMBER_NAME = "이름은 필수입니다.";
        public static final String MEMBER_NICKNAME = "닉네임은 필수입니다.";
        public static final String MEMBER_LOGIN_ID = "아이디는 필수입니다.";
        public static final String MEMBER_PASSWORD = "비밀번호는 필수입니다.";
        public static final String MEMBER_SCHOOL = "학교 이름은 필수입니다.";
        public static final String MEMBER_POSTCODE = "우편번호는 필수입니다.";
        public static final String MEMBER_DEFAULT_ADDRESS = "주소는 필수입니다.";
        public static final String MEMBER_DETAIL_ADDRESS = "상세주소는 필수입니다.";
        public static final String MEMBER_PHONE = "전화번호는 필수입니다.";
        public static final String MEMBER_EMAIL = "이메일은 필수입니다.";
    }

    public static class DuplicateCheck {
        public static final String RESOURCE = "중복 체크 타입[nickname / loginId / phone / email]은 필수입니다.";
        public static final String VALUE = "중복 체크 값은 필수입니다.";
    }

    public static class ChangeNickname {
        public static final String CHANGE_NAME = "변경할 닉네임은 필수입니다";
    }

    public static class FindId {
        public static final String NAME = "이름은 필수입니다.";
        public static final String EMAIL = "이메일은 필수입니다.";
    }

    public static class AuthForResetPassword {
        public static final String NAME = "이름은 필수입니다.";
        public static final String LOGIN_ID = "로그인 아이디는 필수입니다.";
        public static final String EMAIL = "이메일은 필수입니다.";
    }

    public static class ResetPassword {
        public static final String LOGIN_ID = "로그인 아이디는 필수입니다.";
        public static final String CHANGE_PASSWORD = "변경할 비밀번호는 필수입니다.";
    }
}
