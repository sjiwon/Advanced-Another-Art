package com.sjiwon.anotherart.mail.infrastructure;

public interface EmailMetadata {
    // 템플릿 이름
    String AUTH_TEMPLATE = "EmailAuthCodeTemplate";

    // 이메일 제목
    String LOGIN_ID_AUTH_CODE_TITLE = "아이디 찾기 인증번호 메일입니다.";
    String PASSWORD_AUTH_CODE_TITLE = "비밀번호 재설정 인증번호 메일입니다.";
}
