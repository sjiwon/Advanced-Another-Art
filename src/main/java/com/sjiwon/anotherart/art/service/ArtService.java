package com.sjiwon.anotherart.art.service;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.art.domain.UploadImage;
import com.sjiwon.anotherart.art.domain.hashtag.HashtagRepository;
import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.art.service.dto.request.ArtRegisterRequestDto;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.auction.domain.AuctionRepository;
import com.sjiwon.anotherart.auction.domain.Period;
import com.sjiwon.anotherart.auction.domain.record.AuctionRecordRepository;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.global.exception.GlobalErrorCode;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.service.MemberFindService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArtService {
    private final ArtRepository artRepository;
    private final ArtFindService artFindService;
    private final HashtagRepository hashtagRepository;
    private final MemberFindService memberFindService;
    private final AuctionRepository auctionRepository;
    private final AuctionRecordRepository auctionRecordRepository;

    @Value("${file.dir}")
    private String fileDir;

    @Transactional
    public void artRegistration(Long ownerId, ArtRegisterRequestDto request) {
        Member artOwner = memberFindService.findById(ownerId);
        Art art = buildArt(artOwner, request);
        determineAuctionAndPerformAuctionLogic(art, request.getPeriod());
    }

    private Art buildArt(Member artOwner, ArtRegisterRequestDto request) {
        Art art = Art.builder()
                .owner(artOwner)
                .name(request.getName())
                .description(request.getDescription())
                .artType(request.getArtType())
                .price(request.getPrice())
                .uploadImage(UploadImage.from(request.getFile()))
                .build();
        art.applyHashtags(request.getHashtagList());
        processFileUpload(request.getFile(), art.getUploadImage().getStorageName());
        return artRepository.save(art);
    }

    private void processFileUpload(MultipartFile file, String storageName) {
        try {
            file.transferTo(new File(fileDir + storageName));
        } catch (IOException e) {
            throw AnotherArtException.type(GlobalErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private void determineAuctionAndPerformAuctionLogic(Art art, Period period) {
        if (art.isAuctionType()) {
            auctionRepository.save(Auction.initAuction(art, period));
        }
    }

    public void artNameDuplicateCheck(String artName) {
        if (isAlreadyExistsName(artName)) {
            throw AnotherArtException.type(ArtErrorCode.INVALID_ART_NAME);
        }
    }

    private boolean isAlreadyExistsName(String artName) {
        return artRepository.existsByName(artName);
    }

    @Transactional
    public void changeDescription(Long artId, String changeDescription) {
        Art art = artFindService.findById(artId);
        art.changeDescription(changeDescription);
    }

    @Transactional
    public void updateHashtags(Long artId, List<String> hashtagList) {
        Art art = artFindService.findById(artId);
        hashtagRepository.deleteByArtId(artId);
        art.applyHashtags(new HashSet<>(hashtagList));
    }

    @Transactional
    public void deleteArt(Long memberId, Long artId) {
        Art art = artFindService.findById(artId);
        validateArtOwner(art, memberId);
        executeDeleteArt(art);
    }

    private void validateArtOwner(Art art, Long memberId) {
        if (!art.isArtOwner(memberId)) {
            throw AnotherArtException.type(ArtErrorCode.INVALID_ART_DELETE_BY_ANONYMOUS);
        }
    }

    private void executeDeleteArt(Art art) {
        validateSaleProcess(art);
        if (art.isAuctionType()) {
            validateAuctionArtBidRecord(art);
        }
        hashtagRepository.deleteByArtId(art.getId());
        artRepository.deleteById(art.getId());
    }

    private void validateSaleProcess(Art art) {
        if (art.isSoldOut()) {
            throw AnotherArtException.type(ArtErrorCode.ALREADY_SALE);
        }
    }

    private void validateAuctionArtBidRecord(Art art) {
        if (auctionRecordRepository.existsAuctionRecordByArtId(art.getId())) {
            throw AnotherArtException.type(ArtErrorCode.ALREADY_BID_EXISTS);
        }
    }
}
