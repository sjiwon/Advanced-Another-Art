package com.sjiwon.anotherart.art.application.usecase;

import com.sjiwon.anotherart.art.application.usecase.command.RegisterArtCommand;
import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.model.UploadImage;
import com.sjiwon.anotherart.art.domain.service.ArtImageUploader;
import com.sjiwon.anotherart.art.domain.service.ArtReader;
import com.sjiwon.anotherart.art.domain.service.ArtRegister;
import com.sjiwon.anotherart.art.exception.ArtException;
import com.sjiwon.anotherart.global.annotation.UseCase;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.service.MemberReader;
import lombok.RequiredArgsConstructor;

import static com.sjiwon.anotherart.art.exception.ArtExceptionCode.DUPLICATE_NAME;

@UseCase
@RequiredArgsConstructor
public class RegisterArtUseCase {
    private final ArtReader artReader;
    private final ArtImageUploader artImageUploader;
    private final MemberReader memberReader;
    private final ArtRegister artRegister;

    public Long invoke(final RegisterArtCommand command) {
        if (artReader.isNotUniqueName(command.name())) {
            throw new ArtException(DUPLICATE_NAME);
        }

        final UploadImage uploadImage = artImageUploader.uploadImage(command.image());
        final Member owner = memberReader.getById(command.ownerId());
        final Art art = createArt(command, owner, uploadImage);
        return artRegister.execute(art, command.auctionStartDate(), command.auctionEndDate()).getId();
    }

    private Art createArt(final RegisterArtCommand command, final Member owner, final UploadImage uploadImage) {
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
