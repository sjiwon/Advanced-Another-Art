package com.sjiwon.anotherart.member.service.dto.response;

import com.sjiwon.anotherart.art.infra.query.dto.SimpleAuctionArt;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserTradedAuctionArt {
    private SimpleAuctionArt art;
    private List<String> hashtags;
}
