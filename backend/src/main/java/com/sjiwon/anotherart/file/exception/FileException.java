package com.sjiwon.anotherart.file.exception;

import com.sjiwon.anotherart.global.base.BusinessException;
import lombok.Getter;

@Getter
public class FileException extends BusinessException {
    private final FileExceptionCode code;

    public FileException(final FileExceptionCode code) {
        super(code);
        this.code = code;
    }
}
