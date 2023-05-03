package com.sjiwon.anotherart.member.exception;

import com.sjiwon.anotherart.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {
    INVALID_NICKNAME_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER_001", "닉네임 형식에 맞지 않습니다."),
    NICKNAME_SAME_AS_BEFORE(HttpStatus.CONFLICT, "MEMBER_002", "이전과 동일한 닉네임으로 변경할 수 없습니다,"),
    INVALID_PASSWORD_PATTERN(HttpStatus.BAD_REQUEST, "MEMBER_003", "비밀번호 형식에 맞지 않습니다."),
    PASSWORD_SAME_AS_BEFORE(HttpStatus.CONFLICT, "MEMBER_004", "이전과 동일한 비밀번호로 변경할 수 없습니다."),
    INVALID_EMAIL_PATTERN(HttpStatus.BAD_REQUEST, "MEMBER_005", "이메일 형식에 맞지 않습니다."),
    INVALID_POST_CODE(HttpStatus.BAD_REQUEST, "MEMBER_006", "우편번호는 5자리여야 합니다."),
    INVALID_ADDRESS(HttpStatus.BAD_REQUEST, "MEMBER_007", "주소는 비어있지 않아야 합니다."),
    POINT_CANNOT_BE_NEGATIVE(HttpStatus.BAD_REQUEST, "MEMBER_008", "포인트는 음수가 될 수 없습니다."),
    POINT_IS_NOT_ENOUGH(HttpStatus.BAD_REQUEST, "MEMBER_009", "포인트가 부족합니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_010", "사용자 정보가 존재하지 않습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "MEMBER_011", "비밀번호가 일치하지 않습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
