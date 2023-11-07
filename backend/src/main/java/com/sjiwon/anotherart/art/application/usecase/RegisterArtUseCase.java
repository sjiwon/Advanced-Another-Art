package com.sjiwon.anotherart.art.application.usecase;

import com.sjiwon.anotherart.art.application.usecase.command.RegisterArtCommand;
import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.model.UploadImage;
import com.sjiwon.anotherart.art.domain.service.ArtImageUploader;
import com.sjiwon.anotherart.art.domain.service.ArtRegistrationProcessor;
import com.sjiwon.anotherart.art.domain.service.ArtResourceValidator;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterArtUseCase {
    private final ArtResourceValidator artResourceValidator;
    private final ArtImageUploader artImageUploader;
    private final MemberRepository memberRepository;
    private final ArtRegistrationProcessor artRegistrationProcessor;

    public Long invoke(final RegisterArtCommand command) {
        artResourceValidator.validatenNameIsUnique(command.name().getValue());

        final UploadImage uploadImage = artImageUploader.uploadImage(command.image());
        final Member owner = memberRepository.getById(command.ownerId());
        final Art art = build(command, owner, uploadImage);

        return artRegistrationProcessor.execute(art, command.auctionStartDate(), command.auctionEndDate()).getId();
    }

    private Art build(final RegisterArtCommand command, final Member owner, final UploadImage uploadImage) {
        return Art.createArt(
                owner,
                command.name(),
                command.description(),
                command.type(),
                command.price(),
                uploadImage,
                command.hashtags()
        );
    }
}
