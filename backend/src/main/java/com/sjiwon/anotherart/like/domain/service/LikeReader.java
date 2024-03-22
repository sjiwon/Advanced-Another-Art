package com.sjiwon.anotherart.like.domain.service;

import com.sjiwon.anotherart.like.domain.model.Like;
import com.sjiwon.anotherart.like.domain.repository.LikeRepository;
import com.sjiwon.anotherart.like.exception.LikeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.sjiwon.anotherart.like.exception.LikeExceptionCode.FAVORITE_MARKING_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class LikeReader {
    private final LikeRepository likeRepository;

    public Like getLike(final long artId, final long memberId) {
        return likeRepository.findByArtIdAndMemberId(artId, memberId)
                .orElseThrow(() -> new LikeException(FAVORITE_MARKING_NOT_FOUND));
    }
}
