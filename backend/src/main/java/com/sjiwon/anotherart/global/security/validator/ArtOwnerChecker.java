package com.sjiwon.anotherart.global.security.validator;

import com.sjiwon.anotherart.art.domain.ArtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArtOwnerChecker {
    private final ArtRepository artRepository;

    public boolean isArtOwner(final Long memberId, final Long artId) {
        return artRepository.existsByIdAndOwnerId(artId, memberId);
    }
}
