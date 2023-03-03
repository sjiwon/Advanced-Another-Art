package com.sjiwon.anotherart.member.controller.utils;

import com.sjiwon.anotherart.member.controller.dto.request.FindIdRequest;

public class FindIdRequestUtils {
    public static FindIdRequest createRequest(String name, String email) {
        return FindIdRequest.builder()
                .name(name)
                .email(email)
                .build();
    }
}
