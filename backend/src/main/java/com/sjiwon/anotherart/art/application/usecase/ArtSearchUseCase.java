package com.sjiwon.anotherart.art.application.usecase;

import com.sjiwon.anotherart.art.application.usecase.query.GetActiveAuctionArts;
import com.sjiwon.anotherart.art.application.usecase.query.GetArtsByHashtag;
import com.sjiwon.anotherart.art.application.usecase.query.GetArtsByKeyword;
import com.sjiwon.anotherart.art.application.usecase.query.response.AuctionArtResponse;
import com.sjiwon.anotherart.art.application.usecase.query.response.GeneralArtResponse;
import com.sjiwon.anotherart.art.domain.repository.query.ArtDetailQueryRepository;
import com.sjiwon.anotherart.art.domain.repository.query.response.ArtDetails;
import com.sjiwon.anotherart.art.domain.repository.query.response.AuctionArt;
import com.sjiwon.anotherart.art.domain.repository.query.response.GeneralArt;
import com.sjiwon.anotherart.art.domain.repository.query.spec.ActiveAuctionArtsSearchCondition;
import com.sjiwon.anotherart.art.domain.repository.query.spec.ArtDetailsSearchCondition;
import com.sjiwon.anotherart.global.annotation.UseCase;
import com.sjiwon.anotherart.global.query.PageResponse;
import com.sjiwon.anotherart.global.query.Pagination;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@UseCase
@RequiredArgsConstructor
public class ArtSearchUseCase {
    private final ArtDetailQueryRepository artDetailQueryRepository;

    public PageResponse<List<AuctionArtResponse>> getActiveAuctionArts(final GetActiveAuctionArts query) {
        final ActiveAuctionArtsSearchCondition condition = query.createCondition();
        final Pageable pageable = query.createPage();
        final Page<AuctionArt> result = artDetailQueryRepository.fetchActiveAuctionArts(condition, pageable);
        return new PageResponse<>(
                result.getContent()
                        .stream()
                        .map(AuctionArtResponse::from)
                        .toList(),
                createPagination(result, pageable.getPageNumber() + 1)
        );
    }

    public PageResponse<List<AuctionArtResponse>> getAuctionArtsByKeyword(final GetArtsByKeyword query) {
        final ArtDetailsSearchCondition condition = query.createCondition();
        final Pageable pageable = query.createPage();
        final Page<AuctionArt> result = artDetailQueryRepository.fetchAuctionArtsByKeyword(condition, pageable);
        return new PageResponse<>(
                result.getContent()
                        .stream()
                        .map(AuctionArtResponse::from)
                        .toList(),
                createPagination(result, pageable.getPageNumber() + 1)
        );
    }

    public PageResponse<List<GeneralArtResponse>> getGeneralArtsByKeyword(final GetArtsByKeyword query) {
        final ArtDetailsSearchCondition condition = query.createCondition();
        final Pageable pageable = query.createPage();
        final Page<GeneralArt> result = artDetailQueryRepository.fetchGeneralArtsByKeyword(condition, pageable);
        return new PageResponse<>(
                result.getContent()
                        .stream()
                        .map(GeneralArtResponse::from)
                        .toList(),
                createPagination(result, pageable.getPageNumber() + 1)
        );
    }

    public PageResponse<List<AuctionArtResponse>> getAuctionArtsByHashtag(final GetArtsByHashtag query) {
        final ArtDetailsSearchCondition condition = query.createCondition();
        final Pageable pageable = query.createPage();
        final Page<AuctionArt> result = artDetailQueryRepository.fetchAuctionArtsByHashtag(condition, pageable);
        return new PageResponse<>(
                result.getContent()
                        .stream()
                        .map(AuctionArtResponse::from)
                        .toList(),
                createPagination(result, pageable.getPageNumber() + 1)
        );
    }

    public PageResponse<List<GeneralArtResponse>> getGeneralArtsByHashtag(final GetArtsByHashtag query) {
        final ArtDetailsSearchCondition condition = query.createCondition();
        final Pageable pageable = query.createPage();
        final Page<GeneralArt> result = artDetailQueryRepository.fetchGeneralArtsByHashtag(condition, pageable);
        return new PageResponse<>(
                result.getContent()
                        .stream()
                        .map(GeneralArtResponse::from)
                        .toList(),
                createPagination(result, pageable.getPageNumber() + 1)
        );
    }

    private Pagination createPagination(final Page<? extends ArtDetails> result, final int currentPage) {
        return Pagination.of(result.getTotalElements(), result.getTotalPages(), currentPage);
    }
}
