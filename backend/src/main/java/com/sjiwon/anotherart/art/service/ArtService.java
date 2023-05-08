package com.sjiwon.anotherart.art.service;

import com.sjiwon.anotherart.art.controller.dto.request.ArtRegisterRequest;
import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtName;
import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.art.domain.Description;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.auction.domain.AuctionRepository;
import com.sjiwon.anotherart.auction.domain.Period;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.service.MemberFindService;
import com.sjiwon.anotherart.upload.utils.FileUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.sjiwon.anotherart.art.domain.ArtType.AUCTION;
import static com.sjiwon.anotherart.art.domain.ArtType.GENERAL;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArtService {
    private final MemberFindService memberFindService;
    private final FileUploader fileUploader;
    private final ArtValidator artValidator;
    private final ArtRepository artRepository;
    private final AuctionRepository auctionRepository;

    @Transactional
    public Long registerArt(Long ownerId, ArtRegisterRequest request) {
        validateUniqueNameForCreate(request.name());

        Member owner = memberFindService.findById(ownerId);
        String storageName = fileUploader.uploadArtImage(request.file());
        return buildArt(owner, storageName, request);
    }

    private void validateUniqueNameForCreate(String name) {
        artValidator.validateUniqueNameForCreate(ArtName.from(name));
    }

    private Long buildArt(Member owner, String storageName, ArtRegisterRequest request) {
        Art art = artRepository.save(
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

    public void duplicateCheck(String resource, String value) {
        if (resource.equals("name")) {
            artValidator.validateUniqueNameForCreate(ArtName.from(value));
        }
    }
}
