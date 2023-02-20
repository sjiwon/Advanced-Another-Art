package com.sjiwon.anotherart.member.service.dto.response;

import com.sjiwon.anotherart.art.infra.query.dto.BasicAuctionArt;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserWinningAuction {
    private BasicAuctionArt art;
    private List<String> hashtags;
}
