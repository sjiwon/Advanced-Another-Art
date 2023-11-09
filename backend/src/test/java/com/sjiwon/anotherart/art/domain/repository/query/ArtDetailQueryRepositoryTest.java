package com.sjiwon.anotherart.art.domain.repository.query;

import com.sjiwon.anotherart.art.domain.repository.ArtRepository;
import com.sjiwon.anotherart.auction.domain.repository.AuctionRepository;
import com.sjiwon.anotherart.common.RepositoryTest;
import com.sjiwon.anotherart.favorite.domain.repository.FavoriteRepository;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import com.sjiwon.anotherart.purchase.domain.repository.PurchaseRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import(ArtDetailQueryRepositoryImpl.class)
@DisplayName("Art -> ArtDetailQueryRepository 테스트")
class ArtDetailQueryRepositoryTest extends RepositoryTest {
    @Autowired
    private ArtDetailQueryRepositoryImpl sut;

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

    @PersistenceContext
    private EntityManager em;

    @Nested
    @DisplayName("현재 진행중인 경매 작품 리스트 조회 Query")
    class FetchActiveAuctionArts {
        @Test
        @DisplayName("현재 진행중인 경매 작품 리스트를 조회한다")
        void success() {
        }
    }

    @Nested
    @DisplayName("키워드 기반 경매 작품 리스트 조회 Query")
    class FetchAuctionArtsByKeyword {
        @Test
        @DisplayName("키워드 기반 경매 작품 리스트를 조회한다")
        void success() {
        }
    }

    @Nested
    @DisplayName("키워드 기반 일반 작품 리스트 조회 Query")
    class FetchGeneralArtsByKeyword {
        @Test
        @DisplayName("키워드 기반 일반 작품 리스트를 조회한다")
        void success() {
        }
    }

    @Nested
    @DisplayName("해시태그 기반 경매 작품 리스트 조회 Query")
    class FetchAuctionArtsByHashtag {
        @Test
        @DisplayName("해시태그 기반 경매 작품 리스트를 조회한다")
        void success() {
        }
    }

    @Nested
    @DisplayName("해시태그 기반 일반 작품 리스트 조회 Query")
    class FetchGeneralArtsByHashtag {
        @Test
        @DisplayName("해시태그 기반 일반 작품 리스트를 조회한다")
        void success() {
        }
    }
}
