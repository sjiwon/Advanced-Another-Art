package com.sjiwon.anotherart.art.controller.dto.request;

import com.sjiwon.anotherart.global.annotation.ValidArtHashtagSize;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UpdateArtHashtagRequest {
    @ValidArtHashtagSize
    private List<String> hashtagList;
}
