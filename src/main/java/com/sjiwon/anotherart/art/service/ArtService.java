package com.sjiwon.anotherart.art.service;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.art.domain.UploadImage;
import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.art.service.dto.request.ArtRegisterRequestDto;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.auction.domain.AuctionRepository;
import com.sjiwon.anotherart.auction.domain.Period;
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

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArtService {
    private final ArtRepository artRepository;
    private final ArtFindService artFindService;
    private final MemberFindService memberFindService;
    private final AuctionRepository auctionRepository;

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
}
