package com.sjiwon.anotherart.art.service.dto.response;

import com.sjiwon.anotherart.art.infra.query.dto.response.BasicAuctionArt;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AuctionArt implements AbstractArt {
    private BasicAuctionArt art;
    private List<String> hashtags;
    private List<Long> likeMarkingMembers;
    private int bidCount;
}
