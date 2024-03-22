package com.sjiwon.anotherart.member.exception;

import com.sjiwon.anotherart.global.base.BusinessExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Getter
@RequiredArgsConstructor
public enum MemberExceptionCode implements BusinessExceptionCode {
    // Entity Not Found
    MEMBER_NOT_FOUND(NOT_FOUND, "MEMBER_001", "사용자 정보가 존재하지 않습니다."),

    // Domain
    INVALID_NICKNAME_FORMAT(BAD_REQUEST, "MEMBER_002", "닉네임 형식에 맞지 않습니다."),
    NICKNAME_SAME_AS_BEFORE(CONFLICT, "MEMBER_003", "이전과 동일한 닉네임으로 변경할 수 없습니다,"),
    INVALID_PASSWORD_PATTERN(BAD_REQUEST, "MEMBER_004", "비밀번호 형식에 맞지 않습니다."),
    PASSWORD_SAME_AS_BEFORE(CONFLICT, "MEMBER_005", "이전과 동일한 비밀번호로 변경할 수 없습니다."),
    INVALID_PHONE_PATTERN(BAD_REQUEST, "MEMBER_006", "전화번호는 '-'로 구분해서 작성해주세요"),
    INVALID_EMAIL_PATTERN(BAD_REQUEST, "MEMBER_007", "이메일 형식에 맞지 않습니다."),
    INVALID_POST_CODE(BAD_REQUEST, "MEMBER_008", "우편번호는 5자리여야 합니다."),
    INVALID_ADDRESS(BAD_REQUEST, "MEMBER_009", "주소를 입력해주세요."),
    POINT_CANNOT_BE_NEGATIVE(BAD_REQUEST, "MEMBER_010", "포인트는 음수가 될 수 없습니다."),
    POINT_IS_NOT_ENOUGH(CONFLICT, "MEMBER_011", "포인트가 부족합니다."),
    INVALID_DUPLICATE_RESOURCE(BAD_REQUEST, "MEMBER_012", "중복 체크 대상이 아닙니다."),

    // UseCase
    INVALID_PASSWORD(UNAUTHORIZED, "MEMBER_013", "비밀번호가 일치하지 않습니다."),
    DUPLICATE_NICKNAME(CONFLICT, "MEMBER_014", "이미 사용중인 닉네임입니다."),
    DUPLICATE_LOGIN_ID(CONFLICT, "MEMBER_015", "이미 사용중인 아이디입니다."),
    DUPLICATE_PHONE(CONFLICT, "MEMBER_016", "이미 사용중인 전화번호입니다."),
    DUPLICATE_EMAIL(CONFLICT, "MEMBER_017", "이미 사용중인 이메일입니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
