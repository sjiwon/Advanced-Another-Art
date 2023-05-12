package com.sjiwon.anotherart.art.service;

import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.art.infra.query.dto.response.ArtDetails;
import com.sjiwon.anotherart.art.infra.query.dto.response.AuctionArt;
import com.sjiwon.anotherart.art.infra.query.dto.response.GeneralArt;
import com.sjiwon.anotherart.art.service.dto.response.ArtAssembler;
import com.sjiwon.anotherart.art.utils.search.ArtDetailSearchCondition;
import com.sjiwon.anotherart.art.utils.search.Pagination;
import com.sjiwon.anotherart.art.utils.search.SortType;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.sjiwon.anotherart.art.domain.ArtType.AUCTION;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArtSearchService {
    private final ArtRepository artRepository;

    public ArtDetails getArt(Long artId) {
        ArtDetails response = isAuctionType(artId) ? artRepository.getAuctionArt(artId) : artRepository.getGeneralArt(artId);
        validateResponseExists(response);
        return response;
    }

    private boolean isAuctionType(Long artId) {
        return artRepository.getArtTypeById(artId) == AUCTION;
    }

    private void validateResponseExists(ArtDetails response) {
        if (response == null) {
            throw AnotherArtException.type(ArtErrorCode.ART_NOT_FOUND);
        }
    }

    public ArtAssembler getActiveAuctionArts(String sortType, Pageable pageable) {
        Page<AuctionArt> result = artRepository.findActiveAuctionArts(SortType.from(sortType), pageable);
        return assemblingResult(result, pageable.getPageNumber() + 1);
    }

    public ArtAssembler getArtsByKeyword(ArtDetailSearchCondition condition, Pageable pageable) {
        Page<? extends ArtDetails> result = condition.isAuctionType()
                ? getAuctionArtsByKeyword(condition, pageable)
                : getGeneralArtsByKeyword(condition, pageable);
        return assemblingResult(result, pageable.getPageNumber() + 1);
    }

    private Page<AuctionArt> getAuctionArtsByKeyword(ArtDetailSearchCondition condition, Pageable pageable) {
        return artRepository.findAuctionArtsByKeyword(condition, pageable);
    }

    private Page<GeneralArt> getGeneralArtsByKeyword(ArtDetailSearchCondition condition, Pageable pageable) {
        return artRepository.findGeneralArtsByKeyword(condition, pageable);
    }

    public ArtAssembler getArtsByHashtag(ArtDetailSearchCondition condition, Pageable pageable) {
        Page<? extends ArtDetails> result = condition.isAuctionType()
                ? getAuctionArtsByHashtag(condition, pageable)
                : getGeneralArtsByHashtag(condition, pageable);
        return assemblingResult(result, pageable.getPageNumber() + 1);
    }

    private Page<AuctionArt> getAuctionArtsByHashtag(ArtDetailSearchCondition condition, Pageable pageable) {
        return artRepository.findAuctionArtsByHashtag(condition, pageable);
    }

    private Page<GeneralArt> getGeneralArtsByHashtag(ArtDetailSearchCondition condition, Pageable pageable) {
        return artRepository.findGeneralArtsByHashtag(condition, pageable);
    }

    private ArtAssembler assemblingResult(Page<? extends ArtDetails> result, int page) {
        Pagination pagination = Pagination.of(result.getTotalElements(), result.getTotalPages(), page);
        return new ArtAssembler((List<ArtDetails>) result.getContent(), pagination);
    }
}
