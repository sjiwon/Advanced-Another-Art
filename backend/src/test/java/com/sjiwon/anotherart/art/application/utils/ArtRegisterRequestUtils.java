package com.sjiwon.anotherart.art.application.utils;

import com.sjiwon.anotherart.art.presentation.dto.request.ArtRegisterRequest;
import com.sjiwon.anotherart.common.fixture.ArtFixture;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

import static com.sjiwon.anotherart.common.fixture.PeriodFixture.OPEN_WEEK_1_LATER;

public class ArtRegisterRequestUtils {
    public static ArtRegisterRequest createArtRegisterRequestWithInsufficientPrice(final ArtFixture fixture, final MultipartFile file, final Set<String> hashtags) {
        return new ArtRegisterRequest(
                fixture.getName(),
                fixture.getDescription(),
                "general",
                999,
                null,
                null,
                hashtags,
                file
        );
    }

    public static ArtRegisterRequest createGeneralArtRegisterRequest(final ArtFixture fixture, final MultipartFile file, final Set<String> hashtags) {
        return new ArtRegisterRequest(
                fixture.getName(),
                fixture.getDescription(),
                "general",
                fixture.getPrice(),
                null,
                null,
                hashtags,
                file
        );
    }

    public static ArtRegisterRequest createAuctionArtRegisterRequest(final ArtFixture fixture, final MultipartFile file, final Set<String> hashtags) {
        return new ArtRegisterRequest(
                fixture.getName(),
                fixture.getDescription(),
                "auction",
                fixture.getPrice(),
                OPEN_WEEK_1_LATER.getStartDate(),
                OPEN_WEEK_1_LATER.getEndDate(),
                hashtags,
                file
        );
    }
}
