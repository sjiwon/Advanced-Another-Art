package com.sjiwon.anotherart.favorite.service;

import com.sjiwon.anotherart.favorite.domain.Favorite;
import com.sjiwon.anotherart.favorite.domain.FavoriteRepository;
import com.sjiwon.anotherart.favorite.exception.FavoriteErrorCode;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;

    @Transactional
    public Long like(final Long artId, final Long memberId) {
        validateLike(artId, memberId);

        final Favorite favoriteArt = Favorite.favoriteMarking(artId, memberId);
        return favoriteRepository.save(favoriteArt).getId();
    }

    private void validateLike(final Long artId, final Long memberId) {
        if (favoriteRepository.existsByArtIdAndMemberId(artId, memberId)) {
            throw AnotherArtException.type(FavoriteErrorCode.ALREADY_FAVORITE_MARKED);
        }
    }

    @Transactional
    public void cancel(final Long artId, final Long memberId) {
        validateCancel(artId, memberId);
        favoriteRepository.deleteFavoriteMarking(artId, memberId);
    }

    private void validateCancel(final Long artId, final Long memberId) {
        if (!favoriteRepository.existsByArtIdAndMemberId(artId, memberId)) {
            throw AnotherArtException.type(FavoriteErrorCode.NOT_FAVORITE_MARKED);
        }
    }
}
