package com.sjiwon.anotherart.file.exception;

import com.sjiwon.anotherart.global.base.BusinessExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Getter
@RequiredArgsConstructor
public enum FileExceptionCode implements BusinessExceptionCode {
    FILE_IS_NOT_UPLOAD(BAD_REQUEST, "UPLOAD_001", "파일이 전송되지 않았습니다."),
    INVALID_FILE_EXTENSION(BAD_REQUEST, "UPLOAD_002", "제공하지 않는 파일 확장자입니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
