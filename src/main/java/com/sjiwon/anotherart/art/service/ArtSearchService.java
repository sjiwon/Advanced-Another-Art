package com.sjiwon.anotherart.art.service;

import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.art.domain.ArtType;
import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.art.service.dto.response.AbstractArt;
import com.sjiwon.anotherart.art.service.dto.response.AuctionArt;
import com.sjiwon.anotherart.art.service.dto.response.GeneralArt;
import com.sjiwon.anotherart.auction.domain.record.AuctionRecordRepository;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArtSearchService {
    private final ArtRepository artRepository;
    private final AuctionRecordRepository auctionRecordRepository;

    public AbstractArt getSingleArt(Long artId) {
        ArtType artType = getArtType(artId);
        if (artType == ArtType.AUCTION) {
            return AuctionArt.builder()
                    .art(artRepository.findAuctionArtById(artId))
                    .hashtags(artRepository.getHashtagsById(artId))
                    .likeMarkingMembers(artRepository.getLikeMarkingMembersById(artId))
                    .bidCount(auctionRecordRepository.getBidCountByArtId(artId))
                    .build();
        } else if (artType == ArtType.GENERAL) {
            return GeneralArt.builder()
                    .art(artRepository.findGeneralArtById(artId))
                    .hashtags(artRepository.getHashtagsById(artId))
                    .likeMarkingMembers(artRepository.getLikeMarkingMembersById(artId))
                    .build();
        } else {
            throw AnotherArtException.type(ArtErrorCode.ART_NOT_FOUND);
        }
    }

    private ArtType getArtType(Long artId) {
        return artRepository.searchArtType(artId);
    }
}
