package com.sjiwon.anotherart.art.application.usecase;

import com.sjiwon.anotherart.art.application.dto.response.AuctionArtPagingResponse;
import com.sjiwon.anotherart.art.application.dto.response.GeneralArtPagingResponse;
import com.sjiwon.anotherart.art.application.usecase.query.GetActiveAuctionArts;
import com.sjiwon.anotherart.art.application.usecase.query.GetArtsByHashtag;
import com.sjiwon.anotherart.art.application.usecase.query.GetArtsByKeyword;
import com.sjiwon.anotherart.art.domain.repository.query.ArtDetailQueryRepository;
import com.sjiwon.anotherart.art.domain.repository.query.dto.ArtDetails;
import com.sjiwon.anotherart.art.domain.repository.query.dto.AuctionArt;
import com.sjiwon.anotherart.art.domain.repository.query.dto.GeneralArt;
import com.sjiwon.anotherart.global.annotation.UseCase;
import com.sjiwon.anotherart.global.query.Pagination;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

@UseCase
@RequiredArgsConstructor
public class ArtSearchUseCase {
    private final ArtDetailQueryRepository artDetailQueryRepository;

    public AuctionArtPagingResponse getActiveAuctionArts(final GetActiveAuctionArts query) {
        final Page<AuctionArt> result = artDetailQueryRepository.fetchActiveAuctionArts(query.condition(), query.pageable());
        return new AuctionArtPagingResponse(
                result.getContent(),
                extractPagination(result, query.pageable().getPageNumber() + 1)
        );
    }

    public AuctionArtPagingResponse getAuctionArtsByKeyword(final GetArtsByKeyword query) {
        final Page<AuctionArt> result = artDetailQueryRepository.fetchAuctionArtsByKeyword(query.condition(), query.pageable());
        return new AuctionArtPagingResponse(
                result.getContent(),
                extractPagination(result, query.pageable().getPageNumber() + 1)
        );
    }

    public GeneralArtPagingResponse getGeneralArtsByKeyword(final GetArtsByKeyword query) {
        final Page<GeneralArt> result = artDetailQueryRepository.fetchGeneralArtsByKeyword(query.condition(), query.pageable());
        return new GeneralArtPagingResponse(
                result.getContent(),
                extractPagination(result, query.pageable().getPageNumber() + 1)
        );
    }

    public AuctionArtPagingResponse getAuctionArtsByHashtag(final GetArtsByHashtag query) {
        final Page<AuctionArt> result = artDetailQueryRepository.fetchAuctionArtsByHashtag(query.condition(), query.pageable());
        return new AuctionArtPagingResponse(
                result.getContent(),
                extractPagination(result, query.pageable().getPageNumber() + 1)
        );
    }

    public GeneralArtPagingResponse getGeneralArtsByHashtag(final GetArtsByHashtag query) {
        final Page<GeneralArt> result = artDetailQueryRepository.fetchGeneralArtsByHashtag(query.condition(), query.pageable());
        return new GeneralArtPagingResponse(
                result.getContent(),
                extractPagination(result, query.pageable().getPageNumber() + 1)
        );
    }

    private Pagination extractPagination(final Page<? extends ArtDetails> result, final int currentPage) {
        return Pagination.of(result.getTotalElements(), result.getTotalPages(), currentPage);
    }
}
