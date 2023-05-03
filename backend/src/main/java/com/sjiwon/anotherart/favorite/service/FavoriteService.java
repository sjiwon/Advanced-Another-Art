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
    public Long like(Long artId, Long memberId) {
        validateLike(artId, memberId);

        Favorite favoriteStudy = Favorite.favoriteMarking(artId, memberId);
        return favoriteRepository.save(favoriteStudy).getId();
    }

    private void validateLike(Long artId, Long memberId) {
        if (favoriteRepository.existsByArtIdAndMemberId(artId, memberId)) {
            throw AnotherArtException.type(FavoriteErrorCode.ALREADY_FAVORITE_MARKED);
        }
    }

    @Transactional
    public void cancel(Long artId, Long memberId) {
        validateCancel(artId, memberId);
        favoriteRepository.deleteFavoriteMarking(artId, memberId);
    }

    private void validateCancel(Long artId, Long memberId) {
        if (!favoriteRepository.existsByArtIdAndMemberId(artId, memberId)) {
            throw AnotherArtException.type(FavoriteErrorCode.NOT_FAVORITE_MARKED);
        }
    }
}
