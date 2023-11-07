package com.sjiwon.anotherart.favorite.exception;

import com.sjiwon.anotherart.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FavoriteErrorCode implements ErrorCode {
    ALREADY_LIKE_MARKED(HttpStatus.CONFLICT, "FAVORITE_001", "이미 좋아요 등록을 진행한 작품입니다."),
    FAVORITE_MARKING_NOT_FOUND(HttpStatus.NOT_FOUND, "FAVORITE_002", "좋아요 등록을 하지 않은 작품입니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;

}
