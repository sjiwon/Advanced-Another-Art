package com.sjiwon.anotherart.art.controller.utils;

import com.sjiwon.anotherart.art.controller.dto.request.UpdateArtHashtagRequest;

import java.util.List;

public class UpdateArtHashtagRequestUtils {
    public static UpdateArtHashtagRequest createRequest(List<String> hashtagList) {
        return UpdateArtHashtagRequest.builder()
                .hashtagList(hashtagList)
                .build();
    }
}
