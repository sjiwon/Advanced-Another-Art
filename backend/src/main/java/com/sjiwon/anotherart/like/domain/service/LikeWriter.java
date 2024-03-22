package com.sjiwon.anotherart.like.domain.service;

import com.sjiwon.anotherart.like.domain.model.Like;
import com.sjiwon.anotherart.like.domain.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeWriter {
    private final LikeRepository likeRepository;

    public Like save(final Like target) {
        return likeRepository.save(target);
    }

    public void delete(final long id) {
        likeRepository.delete(id);
    }

    public void deleteByArtId(final long artId) {
        likeRepository.deleteByArtId(artId);
    }
}
