package com.sjiwon.anotherart.favorite.exception;

import com.sjiwon.anotherart.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FavoriteErrorCode implements ErrorCode {
    INVALID_LIKE_REQUEST_BY_ART_OWNER(HttpStatus.BAD_REQUEST, "FAVORITE_001", "작품 소유자는 본인의 작품에 대해서 좋아요 등록 및 취소를 할 수 없습니다."),
    ALREADY_LIKE_MARKING(HttpStatus.BAD_REQUEST, "FAVORITE_002", "이미 좋아요 등록을 한 작품입니다."),
    NEVER_OR_ALREADY_CANCEL(HttpStatus.BAD_REQUEST, "FAVORITE_003", "좋아요 등록을 한 적이 없거나 이미 좋아요를 취소한 작품입니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
