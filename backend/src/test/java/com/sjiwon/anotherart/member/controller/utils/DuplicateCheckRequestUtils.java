package com.sjiwon.anotherart.member.controller.utils;

import com.sjiwon.anotherart.member.controller.dto.request.DuplicateCheckRequest;

public class DuplicateCheckRequestUtils {
    public static DuplicateCheckRequest createRequest(String resource, String value) {
        return DuplicateCheckRequest.builder()
                .resource(resource)
                .value(value)
                .build();
    }
}
