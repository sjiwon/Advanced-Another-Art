package com.sjiwon.anotherart.favorite.exception;

import com.sjiwon.anotherart.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FavoriteErrorCode implements ErrorCode {
    INVALID_LIKE_MARKING_BY_ART_OWNER(HttpStatus.BAD_REQUEST, "FAVORITE_001", "작품 소유자는 본인의 작품에 대해서 좋아요 등록을 할 수 없습니다."),
    ALREADY_LIKE_MARKING(HttpStatus.BAD_REQUEST, "FAVORITE_002", "이미 좋아요 등록을 한 작품입니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
