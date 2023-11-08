package com.sjiwon.anotherart.art.domain.repository.query;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.repository.ArtRepository;
import com.sjiwon.anotherart.auction.domain.repository.AuctionRepository;
import com.sjiwon.anotherart.common.RepositoryTest;
import com.sjiwon.anotherart.favorite.domain.repository.FavoriteRepository;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import com.sjiwon.anotherart.purchase.domain.repository.PurchaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static com.sjiwon.anotherart.art.domain.model.ArtType.AUCTION;
import static com.sjiwon.anotherart.art.domain.model.ArtType.GENERAL;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.GENERAL_1;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(ArtSingleQueryRepositoryImpl.class)
@DisplayName("Art -> ArtSingleQueryRepository 테스트")
class ArtSingleQueryRepositoryTest extends RepositoryTest {
    @Autowired
    private ArtSingleQueryRepositoryImpl sut;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ArtRepository artRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Nested
    @DisplayName("작품 타입 조회 Query")
    class GetArtType {
        private Art artA;
        private Art artB;

        @BeforeEach
        void setUp() {
            final Member owner = memberRepository.save(MEMBER_A.toMember());
            artA = artRepository.save(GENERAL_1.toArt(owner));
            artB = artRepository.save(AUCTION_1.toArt(owner));
        }

        @Test
        @DisplayName("작품 타입을 조회한다")
        void success() {
            assertAll(
                    () -> assertThat(sut.getArtType(artA.getId())).isEqualTo(GENERAL),
                    () -> assertThat(sut.getArtType(artB.getId())).isEqualTo(AUCTION)
            );
        }
    }
}
