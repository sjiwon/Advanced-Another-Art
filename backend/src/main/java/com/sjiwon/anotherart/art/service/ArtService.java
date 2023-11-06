package com.sjiwon.anotherart.art.service;

import com.sjiwon.anotherart.art.controller.dto.request.ArtRegisterRequest;
import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtName;
import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.art.domain.Description;
import com.sjiwon.anotherart.art.domain.hashtag.HashtagRepository;
import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.auction.domain.AuctionRepository;
import com.sjiwon.anotherart.auction.domain.Period;
import com.sjiwon.anotherart.file.infrastructure.s3.S3FileUploader;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.service.MemberFindService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.sjiwon.anotherart.art.domain.ArtType.AUCTION;
import static com.sjiwon.anotherart.art.domain.ArtType.GENERAL;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArtService {
    private final ArtFindService artFindService;
    private final MemberFindService memberFindService;
    private final S3FileUploader s3FileUploader;
    private final ArtValidator artValidator;
    private final ArtRepository artRepository;
    private final HashtagRepository hashtagRepository;
    private final AuctionRepository auctionRepository;

    @Transactional
    public Long registerArt(final Long ownerId, final ArtRegisterRequest request) {
        validateUniqueNameForCreate(request.name());

        final Member owner = memberFindService.findById(ownerId);
        final String storageName = s3FileUploader.uploadFile(null); // TODO command - RawFileData 리팩토링
        return buildArt(owner, storageName, request);
    }

    private void validateUniqueNameForCreate(final String name) {
        artValidator.validateUniqueNameForCreate(ArtName.from(name));
    }

    private Long buildArt(final Member owner, final String storageName, final ArtRegisterRequest request) {
        final Art art = artRepository.save(
                Art.createArt(
                        owner,
                        ArtName.from(request.name()),
                        Description.from(request.description()),
                        request.type().equals("general") ? GENERAL : AUCTION,
                        request.price(),
                        storageName,
                        request.hashtags()
                )
        );

        if (art.isAuctionType()) {
            auctionRepository.save(
                    Auction.createAuction(art, Period.of(request.auctionStartDate(), request.auctionEndDate()))
            );
        }

        return art.getId();
    }

    public void duplicateCheck(final String resource, final String value) {
        if (resource.equals("name")) {
            artValidator.validateUniqueNameForCreate(ArtName.from(value));
        }
    }

    @Transactional
    public void update(final Long artId, final String name, final String description, final Set<String> hashtags) {
        validateUniqueNameForUpdate(name, artId);

        final Art art = artFindService.findById(artId);
        art.update(ArtName.from(name), Description.from(description), hashtags);
    }

    private void validateUniqueNameForUpdate(final String name, final Long artId) {
        artValidator.validateUniqueNameForUpdate(ArtName.from(name), artId);
    }

    @Transactional
    public void delete(final Long artId) {
        final Art art = artFindService.findById(artId);
        validateArtIsSold(art);
        validateAuctionRecordIsExists(art);
        proceedToDeleteArt(artId);
    }

    private void validateArtIsSold(final Art art) {
        if (art.isSold()) {
            throw AnotherArtException.type(ArtErrorCode.CANNOT_DELETE_SOLD_ART);
        }
    }

    private void validateAuctionRecordIsExists(final Art art) {
        if (art.isAuctionType() && hasAuctionRecords(art.getId())) {
            throw AnotherArtException.type(ArtErrorCode.CANNOT_DELETE_IF_BID_EXISTS);
        }
    }

    private boolean hasAuctionRecords(final Long artId) {
        return artRepository.isAuctionRecordExists(artId);
    }

    private void proceedToDeleteArt(final Long artId) {
        hashtagRepository.deleteByArtId(artId);
        auctionRepository.deleteByArtId(artId);
        artRepository.deleteById(artId);
    }
}
