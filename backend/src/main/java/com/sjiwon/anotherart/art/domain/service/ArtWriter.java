package com.sjiwon.anotherart.art.domain.service;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.repository.ArtRepository;
import com.sjiwon.anotherart.art.domain.repository.HashtagRepository;
import com.sjiwon.anotherart.global.annotation.AnotherArtWritableTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArtWriter {
    private final ArtRepository artRepository;
    private final HashtagRepository hashtagRepository;

    public Art save(final Art art) {
        return artRepository.save(art);
    }

    @AnotherArtWritableTransactional
    public void deleteArt(final long artId) {
        hashtagRepository.deleteArtHashtags(artId);
        artRepository.deleteArt(artId);
    }
}
