package com.sjiwon.anotherart.art.service;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.art.domain.UploadImage;
import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.art.service.dto.request.ArtRegisterRequestDto;
import com.sjiwon.anotherart.art.utils.FileUploadUtils;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.auction.domain.AuctionRepository;
import com.sjiwon.anotherart.auction.domain.Period;
import com.sjiwon.anotherart.auction.domain.record.AuctionRecordRepository;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.service.MemberFindService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArtService {
    private final ArtRepository artRepository;
    private final ArtFindService artFindService;
    private final MemberFindService memberFindService;
    private final AuctionRepository auctionRepository;
    private final AuctionRecordRepository auctionRecordRepository;
    private final FileUploadUtils fileUploadUtils;

    @Transactional
    public void registerArt(Long ownerId, ArtRegisterRequestDto request) {
        Member owner = memberFindService.findById(ownerId);
        Art art = buildArt(owner, request);
        handleArtTypeAndStoreAuction(art, request.getPeriod());
    }

    private Art buildArt(Member artOwner, ArtRegisterRequestDto request) {
        Art art = Art.builder()
                .owner(artOwner)
                .name(request.getName())
                .description(request.getDescription())
                .artType(request.getArtType())
                .price(request.getPrice())
                .uploadImage(UploadImage.from(request.getFile()))
                .hashtags(new HashSet<>(request.getHashtagList()))
                .build();
        fileUploadUtils.uploadArtImage(request.getFile(), art.getStorageName());
        return artRepository.save(art);
    }

    private void handleArtTypeAndStoreAuction(Art art, Period period) {
        if (art.isAuctionType()) {
            auctionRepository.save(Auction.initAuction(art, period));
        }
    }

    public void checkDuplicateArtName(String name) {
        if (isArtNameAlreadyExists(name)) {
            throw AnotherArtException.type(ArtErrorCode.INVALID_ART_NAME);
        }
    }

    private boolean isArtNameAlreadyExists(String name) {
        return artRepository.existsByName(name);
    }

    @Transactional
    public void changeDescription(Long artId, String changeDescription) {
        Art art = artFindService.findById(artId);
        art.changeDescription(changeDescription);
    }

    @Transactional
    public void updateArtHashtags(Long artId, List<String> hashtagList) {
        Art art = artFindService.findById(artId);
        artRepository.deleteHashtagsByArtId(artId);
        art.applyHashtags(new HashSet<>(hashtagList));
    }

    @Transactional
    public void deleteArt(Long artId, Long memberId) {
        Art art = artFindService.findById(artId);
        validateArtOwner(art, memberId);
        deleteArtProgress(art);
    }

    private void validateArtOwner(Art art, Long memberId) {
        if (!art.isArtOwner(memberId)) {
            throw AnotherArtException.type(ArtErrorCode.INVALID_ART_DELETE_BY_ANONYMOUS);
        }
    }

    private void deleteArtProgress(Art art) {
        validateArtStatus(art);
        if (art.isAuctionType()) {
            validateAuctionBidRecord(art);
        }
        artRepository.deleteHashtagsByArtId(art.getId());
        artRepository.deleteById(art.getId());
    }

    private void validateArtStatus(Art art) {
        if (art.isSoldOut()) {
            throw AnotherArtException.type(ArtErrorCode.ALREADY_SOLD_OUT);
        }
    }

    private void validateAuctionBidRecord(Art art) {
        if (auctionRecordRepository.existsAuctionRecordByArtId(art.getId())) {
            throw AnotherArtException.type(ArtErrorCode.ALREADY_BID_EXISTS);
        }
    }
}
