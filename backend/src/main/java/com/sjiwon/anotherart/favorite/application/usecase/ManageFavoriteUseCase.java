package com.sjiwon.anotherart.favorite.application.usecase;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.repository.ArtRepository;
import com.sjiwon.anotherart.favorite.application.usecase.command.CancelArtLikeCommand;
import com.sjiwon.anotherart.favorite.application.usecase.command.MarkArtLikeCommand;
import com.sjiwon.anotherart.favorite.domain.model.Favorite;
import com.sjiwon.anotherart.favorite.domain.repository.FavoriteRepository;
import com.sjiwon.anotherart.favorite.exception.FavoriteException;
import com.sjiwon.anotherart.global.annotation.UseCase;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;

import static com.sjiwon.anotherart.favorite.exception.FavoriteExceptionCode.ALREADY_LIKE_MARKED;
import static com.sjiwon.anotherart.favorite.exception.FavoriteExceptionCode.FAVORITE_MARKING_NOT_FOUND;

@UseCase
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
            throw new FavoriteException(ALREADY_LIKE_MARKED);
        }
    }

    public void cancelLike(final CancelArtLikeCommand command) {
        final Favorite favorite = favoriteRepository.findByArtIdAndMemberId(command.artId(), command.memberId())
                .orElseThrow(() -> new FavoriteException(FAVORITE_MARKING_NOT_FOUND));
        favoriteRepository.delete(favorite);
    }
}
