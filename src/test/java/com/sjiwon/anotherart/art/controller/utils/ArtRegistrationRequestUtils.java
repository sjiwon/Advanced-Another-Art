package com.sjiwon.anotherart.art.controller.utils;

import com.sjiwon.anotherart.art.controller.dto.request.ArtRegisterRequest;
import com.sjiwon.anotherart.fixture.ArtFixture;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.util.List;

public class ArtRegistrationRequestUtils {
    private static final String GENERAL_TYPE = "general";
    private static final String AUCTION_TYPE = "auction";
    private static final String FILE_PATH = "src/test/resources/images/upload/";

    public static ArtRegisterRequest createGeneralArtRequestWithEmptyImage(ArtFixture generalArt, List<String> hashtagList) {
        return ArtRegisterRequest.builder()
                .artType(GENERAL_TYPE)
                .name(generalArt.getName())
                .description(generalArt.getDescription())
                .price(generalArt.getPrice())
                .file(createEmptyMultipartFile(generalArt))
                .hashtagList(hashtagList)
                .build();
    }

    public static ArtRegisterRequest createGeneralArtRequestWithNotAcceptableFormat(ArtFixture generalArt, List<String> hashtagList) throws Exception {
        return ArtRegisterRequest.builder()
                .artType(GENERAL_TYPE)
                .name(generalArt.getName())
                .description(generalArt.getDescription())
                .price(generalArt.getPrice())
                .file(createNotAcceptableFormatMultipartFile(generalArt))
                .hashtagList(hashtagList)
                .build();
    }

    public static ArtRegisterRequest createGeneralArtRequest(ArtFixture generalArt, List<String> hashtagList) throws Exception {
        return ArtRegisterRequest.builder()
                .artType(GENERAL_TYPE)
                .name(generalArt.getName())
                .description(generalArt.getDescription())
                .price(generalArt.getPrice())
                .file(createMultipartFile(generalArt))
                .hashtagList(hashtagList)
                .build();
    }

    public static ArtRegisterRequest createAuctionArtRequest(ArtFixture auctionArt, List<String> hashtagList) throws Exception {
        return ArtRegisterRequest.builder()
                .artType(AUCTION_TYPE)
                .name(auctionArt.getName())
                .description(auctionArt.getDescription())
                .price(auctionArt.getPrice())
                .file(createMultipartFile(auctionArt))
                .hashtagList(hashtagList)
                .build();
    }

    private static MultipartFile createEmptyMultipartFile(ArtFixture art) {
        return new MockMultipartFile("file", art.getUploadName(), "image/png", (byte[]) null);
    }

    private static MultipartFile createNotAcceptableFormatMultipartFile(ArtFixture art) throws Exception {
        try (FileInputStream stream = new FileInputStream(FILE_PATH + art.getUploadName())) {
            return new MockMultipartFile("file", art.getUploadName(), "image/bmp", stream);
        }
    }

    private static MultipartFile createMultipartFile(ArtFixture art) throws Exception {
        try (FileInputStream stream = new FileInputStream(FILE_PATH + art.getUploadName())) {
            return new MockMultipartFile("file", art.getUploadName(), "image/png", stream);
        }
    }
}
