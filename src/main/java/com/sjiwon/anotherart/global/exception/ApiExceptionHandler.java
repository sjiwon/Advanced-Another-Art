package com.sjiwon.anotherart.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(AnotherArtException.class)
    public ResponseEntity<ErrorResponse> anotherArtException(AnotherArtException exception) {
        ErrorCode code = exception.getCode();
        loggingException(code);
        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.from(code));
    }

    /**
     * 요청 데이터 Validation 전용 ExceptionHandler
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodArgumentNotValidException() {
        return convert(GlobalErrorCode.VALIDATION_ERROR);
    }

    /**
     * 존재하지 않는 Endpoint 전용 ExceptionHandler
     */
    @ExceptionHandler({NoHandlerFoundException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> notSupportedUriException() {
        return convert(GlobalErrorCode.NOT_SUPPORTED_URI_ERROR);
    }

    /**
     * Endpoint HTTP Method 오류 전용 ExceptionHandler
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> notSupportedMethodException() {
        return convert(GlobalErrorCode.NOT_SUPPORTED_METHOD_ERROR);
    }

    /**
     * 내부 서버 오류 전용 ExceptionHandler
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleAnyException() {
        return convert(GlobalErrorCode.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> convert(ErrorCode code) {
        loggingException(code);
        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.from(code));
    }

    private void loggingException(ErrorCode code) {
        log.info("statusCode={} || errorCode={} || message={}",
                code.getStatus().value(), code.getErrorCode(), code.getMessage());
    }
}
