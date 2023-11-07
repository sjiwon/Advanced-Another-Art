package com.sjiwon.anotherart.art.application.utils;

import com.sjiwon.anotherart.art.presentation.dto.request.RegisterArtRequest;
import com.sjiwon.anotherart.common.fixture.ArtFixture;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

import static com.sjiwon.anotherart.common.fixture.PeriodFixture.OPEN_WEEK_1_LATER;

public class ArtRegisterRequestUtils {
    public static RegisterArtRequest createArtRegisterRequestWithInsufficientPrice(final ArtFixture fixture, final MultipartFile file, final Set<String> hashtags) {
        return new RegisterArtRequest(
                fixture.getName().getValue(),
                fixture.getDescription().getValue(),
                "general",
                999,
                null,
                null,
                hashtags,
                file
        );
    }

    public static RegisterArtRequest createGeneralArtRegisterRequest(final ArtFixture fixture, final MultipartFile file, final Set<String> hashtags) {
        return new RegisterArtRequest(
                fixture.getName().getValue(),
                fixture.getDescription().getValue(),
                "general",
                fixture.getPrice(),
                null,
                null,
                hashtags,
                file
        );
    }

    public static RegisterArtRequest createAuctionArtRegisterRequest(final ArtFixture fixture, final MultipartFile file, final Set<String> hashtags) {
        return new RegisterArtRequest(
                fixture.getName().getValue(),
                fixture.getDescription().getValue(),
                "auction",
                fixture.getPrice(),
                OPEN_WEEK_1_LATER.getStartDate(),
                OPEN_WEEK_1_LATER.getEndDate(),
                hashtags,
                file
        );
    }
}
