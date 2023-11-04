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

    public ArtDetails getArt(final Long artId) {
        final ArtDetails response = isAuctionType(artId) ? artRepository.getAuctionArt(artId) : artRepository.getGeneralArt(artId);
        validateResponseExists(response);
        return response;
    }

    private boolean isAuctionType(final Long artId) {
        return artRepository.getArtTypeById(artId) == AUCTION;
    }

    private void validateResponseExists(final ArtDetails response) {
        if (response == null) {
            throw AnotherArtException.type(ArtErrorCode.ART_NOT_FOUND);
        }
    }

    public ArtAssembler getActiveAuctionArts(final String sortType, final Pageable pageable) {
        final Page<AuctionArt> result = artRepository.findActiveAuctionArts(SortType.from(sortType), pageable);
        return assemblingResult(result, pageable.getPageNumber() + 1);
    }

    public ArtAssembler getArtsByKeyword(final ArtDetailSearchCondition condition, final Pageable pageable) {
        final Page<? extends ArtDetails> result = condition.isAuctionType()
                ? getAuctionArtsByKeyword(condition, pageable)
                : getGeneralArtsByKeyword(condition, pageable);
        return assemblingResult(result, pageable.getPageNumber() + 1);
    }

    private Page<AuctionArt> getAuctionArtsByKeyword(final ArtDetailSearchCondition condition, final Pageable pageable) {
        return artRepository.findAuctionArtsByKeyword(condition, pageable);
    }

    private Page<GeneralArt> getGeneralArtsByKeyword(final ArtDetailSearchCondition condition, final Pageable pageable) {
        return artRepository.findGeneralArtsByKeyword(condition, pageable);
    }

    public ArtAssembler getArtsByHashtag(final ArtDetailSearchCondition condition, final Pageable pageable) {
        final Page<? extends ArtDetails> result = condition.isAuctionType()
                ? getAuctionArtsByHashtag(condition, pageable)
                : getGeneralArtsByHashtag(condition, pageable);
        return assemblingResult(result, pageable.getPageNumber() + 1);
    }

    private Page<AuctionArt> getAuctionArtsByHashtag(final ArtDetailSearchCondition condition, final Pageable pageable) {
        return artRepository.findAuctionArtsByHashtag(condition, pageable);
    }

    private Page<GeneralArt> getGeneralArtsByHashtag(final ArtDetailSearchCondition condition, final Pageable pageable) {
        return artRepository.findGeneralArtsByHashtag(condition, pageable);
    }

    private ArtAssembler assemblingResult(final Page<? extends ArtDetails> result, final int page) {
        final Pagination pagination = Pagination.of(result.getTotalElements(), result.getTotalPages(), page);
        return new ArtAssembler((List<ArtDetails>) result.getContent(), pagination);
    }
}
