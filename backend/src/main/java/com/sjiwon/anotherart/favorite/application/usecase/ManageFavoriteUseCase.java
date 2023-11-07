package com.sjiwon.anotherart.favorite.application.usecase;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.repository.ArtRepository;
import com.sjiwon.anotherart.favorite.application.usecase.command.CancelArtLikeCommand;
import com.sjiwon.anotherart.favorite.application.usecase.command.MarkArtLikeCommand;
import com.sjiwon.anotherart.favorite.domain.model.Favorite;
import com.sjiwon.anotherart.favorite.domain.repository.FavoriteRepository;
import com.sjiwon.anotherart.favorite.exception.FavoriteErrorCode;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManageFavoriteUseCase {
    private final ArtRepository artRepository;
    private final MemberRepository memberRepository;
    private final FavoriteRepository favoriteRepository;

    public Long markLike(final MarkArtLikeCommand command) {
        final Art art = artRepository.getById(command.artId());
        final Member member = memberRepository.getById(command.memberId());

        try {
            return favoriteRepository.save(Favorite.favoriteMarking(art, member)).getId();
        } catch (final DataIntegrityViolationException e) {
            throw AnotherArtException.type(FavoriteErrorCode.ALREADY_LIKE_MARKED);
        }
    }

    public void cancelLike(final CancelArtLikeCommand command) {
        final Favorite favorite = favoriteRepository.findByArtIdAndMemberId(command.artId(), command.memberId())
                .orElseThrow(() -> AnotherArtException.type(FavoriteErrorCode.FAVORITE_MARKING_NOT_FOUND));
        favoriteRepository.delete(favorite);
    }
}
