package com.sjiwon.anotherart.art.service;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.common.ServiceTest;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.upload.utils.FileUploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

import static com.sjiwon.anotherart.art.controller.utils.ArtRegisterRequestUtils.createAuctionArtRegisterRequest;
import static com.sjiwon.anotherart.art.controller.utils.ArtRegisterRequestUtils.createGeneralArtRegisterRequest;
import static com.sjiwon.anotherart.art.domain.ArtStatus.ON_SALE;
import static com.sjiwon.anotherart.art.domain.ArtType.AUCTION;
import static com.sjiwon.anotherart.art.domain.ArtType.GENERAL;
import static com.sjiwon.anotherart.common.utils.FileMockingUtils.createSingleMockMultipartFile;
import static com.sjiwon.anotherart.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.fixture.ArtFixture.GENERAL_1;
import static com.sjiwon.anotherart.fixture.MemberFixture.MEMBER_A;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

@DisplayName("Art [Service Layer] -> ArtService 테스트")
class ArtServiceTest extends ServiceTest {
    @Autowired
    private ArtService artService;

    @MockBean
    private FileUploader fileUploader;

    private Member owner;

    @BeforeEach
    void setUp() {
        owner = memberRepository.save(MEMBER_A.toMember());
    }

    @Nested
    @DisplayName("작품 등록")
    class register {
        @Test
        @DisplayName("이미 사용하고 있는 작품명이면 등록에 실패한다")
        void throwExceptionByDuplicateName() throws IOException {
            // given
            final Set<String> hashtags = Set.of("A", "B", "C", "D");
            final MultipartFile file = createSingleMockMultipartFile("1.png", "image/png");
            final String uploadLink = "https://kr.object.ncloudstorage.com/bucket/arts/uuid.png";
            given(fileUploader.uploadArtImage(file)).willReturn(uploadLink);

            artService.registerArt(owner.getId(), createGeneralArtRegisterRequest(GENERAL_1, file, hashtags));

            // when - then
            assertThatThrownBy(() -> artService.registerArt(owner.getId(), createGeneralArtRegisterRequest(GENERAL_1, file, hashtags)))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(ArtErrorCode.DUPLICATE_NAME.getMessage());
        }

        @Test
        @DisplayName("일반 작품을 등록한다")
        void successGeneral() throws IOException {
            // given
            final Set<String> hashtags = Set.of("A", "B", "C", "D");
            final MultipartFile file = createSingleMockMultipartFile("1.png", "image/png");
            final String uploadLink = "https://kr.object.ncloudstorage.com/bucket/arts/uuid.png";
            given(fileUploader.uploadArtImage(file)).willReturn(uploadLink);

            // when
            Long artId = artService.registerArt(owner.getId(), createGeneralArtRegisterRequest(GENERAL_1, file, hashtags));

            // then
            Art findArt = artRepository.findById(artId).orElseThrow();
            assertAll(
                    () -> assertThat(findArt.getNameValue()).isEqualTo(GENERAL_1.getName()),
                    () -> assertThat(findArt.getDescriptionValue()).isEqualTo(GENERAL_1.getDescription()),
                    () -> assertThat(findArt.getType()).isEqualTo(GENERAL),
                    () -> assertThat(findArt.getPrice()).isEqualTo(GENERAL_1.getPrice()),
                    () -> assertThat(findArt.getStorageName()).isEqualTo(uploadLink),
                    () -> assertThat(findArt.getStatus()).isEqualTo(ON_SALE),
                    () -> assertThat(findArt.getOwner()).isEqualTo(owner),
                    () -> assertThat(findArt.getHashtags()).containsExactlyInAnyOrderElementsOf(hashtags)
            );
        }

        @Test
        @DisplayName("경매 작품을 등록한다")
        void successAuction() throws IOException {
            // given
            final Set<String> hashtags = Set.of("A", "B", "C", "D");
            final MultipartFile file = createSingleMockMultipartFile("1.png", "image/png");
            final String uploadLink = "https://kr.object.ncloudstorage.com/bucket/arts/uuid.png";
            given(fileUploader.uploadArtImage(file)).willReturn(uploadLink);

            // when
            Long artId = artService.registerArt(owner.getId(), createAuctionArtRegisterRequest(AUCTION_1, file, hashtags));

            // then
            Art findArt = artRepository.findById(artId).orElseThrow();
            assertAll(
                    () -> assertThat(findArt.getNameValue()).isEqualTo(AUCTION_1.getName()),
                    () -> assertThat(findArt.getDescriptionValue()).isEqualTo(AUCTION_1.getDescription()),
                    () -> assertThat(findArt.getType()).isEqualTo(AUCTION),
                    () -> assertThat(findArt.getPrice()).isEqualTo(AUCTION_1.getPrice()),
                    () -> assertThat(findArt.getStorageName()).isEqualTo(uploadLink),
                    () -> assertThat(findArt.getStatus()).isEqualTo(ON_SALE),
                    () -> assertThat(findArt.getOwner()).isEqualTo(owner),
                    () -> assertThat(findArt.getHashtags()).containsExactlyInAnyOrderElementsOf(hashtags)
            );

            Auction findAuction = auctionRepository.findByArtId(artId).orElseThrow();
            assertAll(
                    () -> assertThat(findAuction.getArt()).isEqualTo(findArt),
                    () -> assertThat(findAuction.getAuctionRecords()).hasSize(0),
                    () -> assertThat(findAuction.getHighestBidder()).isNull(),
                    () -> assertThat(findAuction.getHighestBidPrice()).isEqualTo(AUCTION_1.getPrice())
            );
        }
    }
}
