package com.sjiwon.anotherart.art.infra.query.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.sjiwon.anotherart.art.domain.ArtName;
import com.sjiwon.anotherart.art.domain.ArtStatus;
import com.sjiwon.anotherart.art.domain.Description;
import com.sjiwon.anotherart.member.domain.model.Nickname;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class GeneralArt implements ArtDetails {
    private final BasicArt art;
//    private final BasicMember owner;
//    private final BasicMember buyer;

    @QueryProjection
    public GeneralArt(
            final Long artId, final ArtName artName, final Description artDescription, final int price, final ArtStatus status, final String storageName, final LocalDateTime registrationDate,
            final Long ownerId, final Nickname ownerNickname, final String ownerSchool,
            final Long buyerId, final Nickname buyerNickname, final String buyerSchool
    ) {
        this.art = new BasicArt(
                artId,
                artName,
                artDescription,
                price,
                status,
                storageName,
                registrationDate
        );

//        this.owner = new BasicMember(
//                ownerId,
//                ownerNickname,
//                ownerSchool
//        );

//        this.buyer = buyerId != null
//                ? new BasicMember(buyerId, buyerNickname, buyerSchool)
//                : null;
    }

    public void applyHashtags(final List<String> hashtags) {
        this.art.applyHashtags(hashtags);
    }

    public void applyLikeMembers(final List<Long> likeMembers) {
        this.art.applyLikeMembers(likeMembers);
    }
}
