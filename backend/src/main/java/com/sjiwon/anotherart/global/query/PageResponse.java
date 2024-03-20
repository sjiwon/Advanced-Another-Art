package com.sjiwon.anotherart.global.query;

public record PageResponse<T>(
        T result,
        Pagination pagination
) {
}
