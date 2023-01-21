package com.sjiwon.anotherart.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(AnotherArtException.class)
    public ResponseEntity<ErrorResponse> catchAnotherArtException(AnotherArtException exception) {
        return ResponseEntity
                .status(exception.getStatusCode())
                .body(ErrorResponse.from(exception));
    }
}
