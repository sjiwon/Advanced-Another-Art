package com.sjiwon.anotherart.art.controller.utils;

import com.sjiwon.anotherart.art.controller.dto.request.ChangeArtDescriptionRequest;

public class ChangeArtDescriptionRequestUtils {
    public static ChangeArtDescriptionRequest createRequest(String changeDescription) {
        return ChangeArtDescriptionRequest.builder()
                .changeDescription(changeDescription)
                .build();
    }
}
