package com.sjiwon.anotherart.art.domain.repository.query;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Art -> ArtDetailQueryRepository [fetchAuctionArtsByKeyword] 테스트")
public class ArtDetailQueryRepositoryFetchAuctionArtsByKeywordTest extends ArtDetailQueryRepositoryTestSupporter {
    @Nested
    @DisplayName("키워드 기반 경매 작품 리스트 조회 Query")
    class FetchAuctionArtsByKeyword {
        @Test
        @DisplayName("키워드 기반 경매 작품 리스트를 조회한다 -> 1) 등록 날짜 오름차순")
        void dateAsc() {
        }

        @Test
        @DisplayName("키워드 기반 경매 작품 리스트를 조회한다 -> 2) 등록 날짜 내림차순")
        void dateDesc() {
        }

        @Test
        @DisplayName("키워드 기반 경매 작품 리스트를 조회한다 -> 3) 최고 입찰가 오름차순")
        void priceAsc() {
        }

        @Test
        @DisplayName("키워드 기반 경매 작품 리스트를 조회한다 -> 4) 최고 입찰가 내림차순")
        void priceDesc() {
        }

        @Test
        @DisplayName("키워드 기반 경매 작품 리스트를 조회한다 -> 5) 입찰 횟수 오름차순")
        void bidCountAsc() {
        }

        @Test
        @DisplayName("키워드 기반 경매 작품 리스트를 조회한다 -> 6) 입찰 횟수 내림차순")
        void bidCountDesc() {
        }

        @Test
        @DisplayName("키워드 기반 경매 작품 리스트를 조회한다 -> 7) 좋아요 횟수 오름차순")
        void likeAsc() {
        }

        @Test
        @DisplayName("키워드 기반 경매 작품 리스트를 조회한다 -> 8) 좋아요 횟수 내림차순")
        void likeDesc() {
        }
    }
}
