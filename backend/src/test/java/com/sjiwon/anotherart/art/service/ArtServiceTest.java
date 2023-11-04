package com.sjiwon.anotherart.art.service;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.common.ServiceTest;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.upload.utils.S3FileUploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
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
import static com.sjiwon.anotherart.fixture.MemberFixture.MEMBER_B;
import static com.sjiwon.anotherart.fixture.PeriodFixture.OPEN_NOW;
import static com.sjiwon.anotherart.member.domain.point.PointType.CHARGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;

@DisplayName("Art [Service Layer] -> ArtService 테스트")
class ArtServiceTest extends ServiceTest {
    @Autowired
    private ArtService artService;

    @MockBean
    private S3FileUploader s3FileUploader;

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
            given(s3FileUploader.uploadFile(file)).willReturn(uploadLink);

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
            given(s3FileUploader.uploadFile(file)).willReturn(uploadLink);

            // when
            final Long artId = artService.registerArt(owner.getId(), createGeneralArtRegisterRequest(GENERAL_1, file, hashtags));

            // then
            final Art findArt = artRepository.findById(artId).orElseThrow();
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
            given(s3FileUploader.uploadFile(file)).willReturn(uploadLink);

            // when
            final Long artId = artService.registerArt(owner.getId(), createAuctionArtRegisterRequest(AUCTION_1, file, hashtags));

            // then
            final Art findArt = artRepository.findById(artId).orElseThrow();
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

            final Auction findAuction = auctionRepository.findByArtId(artId).orElseThrow();
            assertAll(
                    () -> assertThat(findAuction.getArt()).isEqualTo(findArt),
                    () -> assertThat(findAuction.getAuctionRecords()).hasSize(0),
                    () -> assertThat(findAuction.getHighestBidder()).isNull(),
                    () -> assertThat(findAuction.getHighestBidPrice()).isEqualTo(AUCTION_1.getPrice())
            );
        }
    }

    @Nested
    @DisplayName("중복 체크")
    class duplicateCheck {
        private Art art;

        @BeforeEach
        void setUp() {
            art = artRepository.save(AUCTION_1.toArt(owner));
        }

        @Test
        @DisplayName("작품명 중복 체크를 진행한다")
        void checkName() {
            // given
            final String same = art.getNameValue();
            final String diff = "fake";

            // when - then
            assertThatThrownBy(() -> artService.duplicateCheck("name", same))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(ArtErrorCode.DUPLICATE_NAME.getMessage());
            assertDoesNotThrow(() -> artService.duplicateCheck("name", diff));
        }
    }

    @Test
    @DisplayName("작품 정보를 수정한다 [작품명, 설명, 해시태그]")
    void update() {
        // given
        final Art art = artRepository.save(AUCTION_1.toArt(owner));

        // when
        final String updateName = "hello";
        final String updateDescription = "hello world";
        final Set<String> updateHashtags = Set.of("asdf", "asdflksdf", "wlejqkwpe");
        artService.update(art.getId(), updateName, updateDescription, updateHashtags);

        // then
        assertAll(
                () -> assertThat(art.getNameValue()).isEqualTo(updateName),
                () -> assertThat(art.getDescriptionValue()).isEqualTo(updateDescription),
                () -> assertThat(art.getHashtags()).containsExactlyInAnyOrderElementsOf(updateHashtags)
        );
    }

    @Nested
    @DisplayName("작품 삭제")
    class delete {
        private Member bidder;
        private Art art;
        private Auction auction;

        @BeforeEach
        void setUp() {
            bidder = memberRepository.save(MEMBER_B.toMember());
            bidder.addPointRecords(CHARGE, 100_000_000);

            art = artRepository.save(AUCTION_1.toArt(owner));
            auction = auctionRepository.save(Auction.createAuction(art, OPEN_NOW.toPeriod()));
        }

        @Test
        @DisplayName("작품이 판매되었다면 삭제할 수 없다")
        void throwExceptionByCannotDeleteSoldArt() {
            // given
            art.closeSale();

            // when - then
            assertThatThrownBy(() -> artService.delete(art.getId()))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(ArtErrorCode.CANNOT_DELETE_SOLD_ART.getMessage());
        }

        @Test
        @DisplayName("입찰 기록이 존재하는 경매 작품은 삭제할 수 없다")
        void throwExceptionByCannotDeleteIfBidExists() {
            // given
            auction.applyNewBid(bidder, auction.getHighestBidPrice());

            // when - then
            assertThatThrownBy(() -> artService.delete(art.getId()))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(ArtErrorCode.CANNOT_DELETE_IF_BID_EXISTS.getMessage());
        }

        @Test
        @DisplayName("삭제에 성공한다")
        void success() {
            // when
            artService.delete(art.getId());

            // then
            final Optional<Art> findArt = artRepository.findById(art.getId());
            assertThat(findArt).isEmpty();
        }
    }
}
