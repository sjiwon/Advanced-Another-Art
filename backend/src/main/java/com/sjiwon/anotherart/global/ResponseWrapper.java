package com.sjiwon.anotherart.global;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class ResponseWrapper<T> {
    private final T result;

    public static <T> ResponseWrapper<T> from(final T result) {
        return new ResponseWrapper<>(result);
    }
}
