package com.sjiwon.anotherart.global.query;

public record SliceResponse<T>(
        T result,
        boolean hasNext
) {
}
