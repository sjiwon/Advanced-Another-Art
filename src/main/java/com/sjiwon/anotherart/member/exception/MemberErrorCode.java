package com.sjiwon.anotherart.member.exception;

import com.sjiwon.anotherart.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {
    INVALID_PASSWORD_PATTERN(HttpStatus.BAD_REQUEST, "MEMBER_001", "패스워드는 8자 이상 30자 이하이고 알파벳 대소문자, 숫자, 특수문자를 각각 하나이상 포함해야 합니다."),
    INVALID_POST_CODE(HttpStatus.BAD_REQUEST, "MEMBER_002", "우편번호는 5자리여야 합니다."),
    INVALID_ADDRESS(HttpStatus.BAD_REQUEST, "MEMBER_003", "주소는 비어있지 않아야 합니다."),
    INVALID_AVAILABLE_POINTS(HttpStatus.BAD_REQUEST, "MEMBER_004", "사용 가능한 포인트는 0원 이상이여야 합니다."),
    INVALID_POINT_DECREASE(HttpStatus.BAD_REQUEST, "MEMBER_005", "포인트가 부족합니다."),
    INVALID_EMAIL_PATTERN(HttpStatus.BAD_REQUEST, "MEMBER_006", "이메일 형식에 맞지 않습니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "MEMBER_007", "중복된 닉네임입니다."),
    DUPLICATE_LOGIN_ID(HttpStatus.CONFLICT, "MEMBER_008", "중복된 로그인 아이디입니다."),
    DUPLICATE_PHONE(HttpStatus.CONFLICT, "MEMBER_009", "중복된 전화번호입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "MEMBER_010", "중복된 이메일입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_011", "사용자 정보가 존재하지 않습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "MEMBER_012", "비밀번호가 일치하지 않습니다."),
    NICKNAME_SAME_AS_BEFORE(HttpStatus.CONFLICT, "MEMBER_013", "이전과 동일한 닉네임으로 변경할 수 없습니다"),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
