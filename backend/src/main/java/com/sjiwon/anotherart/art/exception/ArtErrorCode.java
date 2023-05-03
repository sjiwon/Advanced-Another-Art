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
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
