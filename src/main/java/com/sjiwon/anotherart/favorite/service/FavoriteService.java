package com.sjiwon.anotherart.favorite.service;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.service.ArtFindService;
import com.sjiwon.anotherart.favorite.domain.Favorite;
import com.sjiwon.anotherart.favorite.domain.FavoriteRepository;
import com.sjiwon.anotherart.favorite.exception.FavoriteErrorCode;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.service.MemberFindService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FavoriteService {
    private final ArtFindService artFindService;
    private final MemberFindService memberFindService;
    private final FavoriteRepository favoriteRepository;

    public void likeMarking(Long artId, Long memberId) {
        Art art = artFindService.findById(artId);
        validateArtOwner(art, memberId);
        validateAlreadyMarking(artId, memberId);
        favoriteRepository.save(Favorite.favoriteMarking(art.getId(), memberId));
    }

    private void validateArtOwner(Art art, Long memberId) {
        if (art.isArtOwner(memberId)) {
            throw AnotherArtException.type(FavoriteErrorCode.INVALID_LIKE_MARKING_BY_ART_OWNER);
        }
    }

    private void validateAlreadyMarking(Long artId, Long memberId) {
        if (favoriteRepository.existsByArtIdAndMemberId(artId, memberId)) {
            throw AnotherArtException.type(FavoriteErrorCode.ALREADY_LIKE_MARKING);
        }
    }
}
