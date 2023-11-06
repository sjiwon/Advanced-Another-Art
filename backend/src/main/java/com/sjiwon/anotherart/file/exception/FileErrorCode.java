package com.sjiwon.anotherart.file.exception;

import com.sjiwon.anotherart.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Getter
@RequiredArgsConstructor
public enum FileErrorCode implements ErrorCode {
    FILE_IS_NOT_UPLOAD(BAD_REQUEST, "UPLOAD_001", "파일이 전송되지 않았습니다."),
    UPLOAD_FAILURE(INTERNAL_SERVER_ERROR, "UPLOAD_002", "서버 내부 오류로 인해 파일 업로드에 실패했습니다."),
    INVALID_FILE_EXTENSION(BAD_REQUEST, "UPLOAD_003", "제공하지 않는 파일 확장자입니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
