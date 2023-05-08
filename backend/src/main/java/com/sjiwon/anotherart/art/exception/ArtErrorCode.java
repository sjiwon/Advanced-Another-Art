package com.sjiwon.anotherart.art.exception;

import com.sjiwon.anotherart.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ArtErrorCode implements ErrorCode {
    NAME_IS_BLANK(HttpStatus.BAD_REQUEST, "ART_001", "작품명은 공백을 허용하지 않습니다."),
    NAME_LENGTH_OUT_OF_RANGE(HttpStatus.CONFLICT, "ART_002", "작품명은 최대 20자까지 가능합니다."),
    DESCRIPTION_IS_BLANK(HttpStatus.BAD_REQUEST, "ART_003", "작품 설명은 공백을 허용하지 않습니다."),
    DESCRIPTION_LENGTH_OUT_OF_RANGE(HttpStatus.CONFLICT, "ART_004", "작품 설명은 최대 1000자까지 가능합니다."),
    ART_NOT_FOUND(HttpStatus.NOT_FOUND, "ART_005", "작품 정보가 존재하지 않습니다."),
    DUPLICATE_NAME(HttpStatus.CONFLICT, "ART_006", "이미 사용중인 작품명입니다."),
    SORT_TYPE_NOT_PROVIED(HttpStatus.BAD_REQUEST, "ART_007", "제공하지 않는 정렬 타입입니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
