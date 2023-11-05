package com.sjiwon.anotherart.global.security.validator;

import com.sjiwon.anotherart.art.domain.ArtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArtOwnerValidator {
    private final ArtRepository artRepository;

    public boolean isArtOwner(final Long artId, final Long memberId) {
        return artRepository.isOwner(artId, memberId);
    }
}
