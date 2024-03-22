package com.sjiwon.anotherart.like.application.usecase;

import com.sjiwon.anotherart.global.annotation.UseCase;
import com.sjiwon.anotherart.like.application.usecase.command.CancelArtLikeCommand;
import com.sjiwon.anotherart.like.domain.model.Like;
import com.sjiwon.anotherart.like.domain.service.LikeReader;
import com.sjiwon.anotherart.like.domain.service.LikeWriter;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class CancelArtLikeUseCase {
    private final LikeReader likeReader;
    private final LikeWriter likeWriter;

    public void invoke(final CancelArtLikeCommand command) {
        final Like like = likeReader.getLike(command.artId(), command.memberId());
        likeWriter.delete(like.getId());
    }
}
