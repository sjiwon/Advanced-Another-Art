package com.sjiwon.anotherart.like.exception;

import com.sjiwon.anotherart.global.base.BusinessExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@RequiredArgsConstructor
public enum LikeExceptionCode implements BusinessExceptionCode {
    // Entity Not Found
    FAVORITE_MARKING_NOT_FOUND(NOT_FOUND, "LIKE_001", "좋아요 등록을 하지 않은 작품입니다."),

    // UseCase
    ALREADY_LIKE_MARKED(CONFLICT, "LIKE_002", "이미 좋아요 등록을 진행한 작품입니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
