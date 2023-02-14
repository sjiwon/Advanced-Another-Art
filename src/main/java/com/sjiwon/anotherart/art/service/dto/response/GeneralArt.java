package com.sjiwon.anotherart.art.service.dto.response;

import com.sjiwon.anotherart.art.infra.query.dto.BasicGeneralArt;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GeneralArt implements AbstractArt {
    private BasicGeneralArt art;
    private List<String> hashtags;
    private List<Long> likeMarkingMembers;
}
