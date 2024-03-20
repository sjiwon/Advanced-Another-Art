package com.sjiwon.anotherart.global.base;

import org.springframework.http.HttpStatus;

public interface BusinessExceptionCode {
    HttpStatus getStatus();

    String getErrorCode();

    String getMessage();
}
