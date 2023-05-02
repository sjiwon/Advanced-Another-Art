package com.sjiwon.anotherart.art.controller.dto.request;

import com.sjiwon.anotherart.global.annotation.validation.ValidHashtagCount;

import java.util.List;

public record UpdateArtHashtagRequest(
        @ValidHashtagCount
        List<String> hashtagList
) {
}
