package com.sjiwon.anotherart.like.application.usecase;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.service.ArtReader;
import com.sjiwon.anotherart.global.annotation.UseCase;
import com.sjiwon.anotherart.like.application.usecase.command.MarkArtLikeCommand;
import com.sjiwon.anotherart.like.domain.model.Like;
import com.sjiwon.anotherart.like.domain.service.LikeWriter;
import com.sjiwon.anotherart.like.exception.LikeException;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.service.MemberReader;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;

import static com.sjiwon.anotherart.like.exception.LikeExceptionCode.ALREADY_LIKE_MARKED;

@UseCase
@RequiredArgsConstructor
public class MarkArtLikeUseCase {
    private final ArtReader artReader;
    private final MemberReader memberReader;
    private final LikeWriter likeWriter;

    public Long invoke(final MarkArtLikeCommand command) {
        final Art art = artReader.getById(command.artId());
        final Member member = memberReader.getById(command.memberId());

        try {
            return likeWriter.save(new Like(art, member)).getId();
        } catch (final DataIntegrityViolationException e) {
            throw new LikeException(ALREADY_LIKE_MARKED);
        }
    }
}
