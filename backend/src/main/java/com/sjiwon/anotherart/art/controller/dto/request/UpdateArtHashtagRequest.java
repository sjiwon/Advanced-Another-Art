package com.sjiwon.anotherart.art.controller.dto.request;

import com.sjiwon.anotherart.global.annotation.validation.ValidHashtagCount;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UpdateArtHashtagRequest {
    @ValidHashtagCount
    private List<String> hashtagList;
}
