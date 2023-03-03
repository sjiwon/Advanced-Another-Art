package com.sjiwon.anotherart.member.service.dto.response;

import com.sjiwon.anotherart.art.infra.query.dto.SimpleTradedArt;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserTradedArt {
    private SimpleTradedArt art;
    private List<String> hashtags;
}
