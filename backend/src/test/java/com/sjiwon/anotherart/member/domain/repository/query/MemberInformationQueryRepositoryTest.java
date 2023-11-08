package com.sjiwon.anotherart.member.domain.repository.query;

import com.sjiwon.anotherart.art.domain.repository.ArtRepository;
import com.sjiwon.anotherart.auction.domain.repository.AuctionRepository;
import com.sjiwon.anotherart.common.RepositoryTest;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import com.sjiwon.anotherart.member.domain.repository.query.dto.MemberInformation;
import com.sjiwon.anotherart.point.domain.repository.PointRecordRepository;
import com.sjiwon.anotherart.purchase.domain.repository.PurchaseRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(MemberInformationQueryRepositoryImpl.class)
@DisplayName("Member -> MemberInformationQueryRepository 테스트")
class MemberInformationQueryRepositoryTest extends RepositoryTest {
    @Autowired
    private MemberInformationQueryRepositoryImpl sut;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PointRecordRepository pointRecordRepository;

    @Autowired
    private ArtRepository artRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @PersistenceContext
    private EntityManager em;

    private static final int MEMBER_INIT_POINT = 1_000_000;

    @Nested
    @DisplayName("사용자 기본 정보 조회 Query")
    class FetchInformation {
        @Test
        @DisplayName("사용자 기본 정보를 조회한다")
        void success() {
            // given
            final Member member = memberRepository.save(MEMBER_A.toMember());
            member.increaseTotalPoint(MEMBER_INIT_POINT);
            member.decreaseAvailablePoint(40_000);

            // when
            final MemberInformation result = sut.fetchInformation(member.getId());

            // then
            assertAll(
                    () -> assertThat(result.getId()).isEqualTo(member.getId()),
                    () -> assertThat(result.getName()).isEqualTo(member.getName()),
                    () -> assertThat(result.getNickname()).isEqualTo(member.getNickname().getValue()),
                    () -> assertThat(result.getLoginId()).isEqualTo(member.getLoginId()),
                    () -> assertThat(result.getSchool()).isEqualTo(member.getSchool()),
                    () -> assertThat(result.getPhone()).isEqualTo(member.getPhone().getValue()),
                    () -> assertThat(result.getEmail()).isEqualTo(member.getEmail().getValue()),
                    () -> assertThat(result.getAddress()).isEqualTo(member.getAddress()),
                    () -> assertThat(result.getTotalPoint()).isEqualTo(MEMBER_INIT_POINT),
                    () -> assertThat(result.getAvailablePoint()).isEqualTo(MEMBER_INIT_POINT - 40_000)
            );
        }
    }

    @Nested
    @DisplayName("포인트 활용 내역 조회 Query")
    class FetchPointRecords {
        @Test
        @DisplayName("포인트 활용 내역을 조회한다")
        void success() {
        }
    }

    @Nested
    @DisplayName("낙찰된 경매 작품 조회 Query")
    class FetchWinningAuctionArts {
        @Test
        @DisplayName("낙찰된 경매 작품을 조회한다")
        void success() {
        }
    }

    @Nested
    @DisplayName("판매한 작품 조회 Query")
    class FetchSoldArtsByType {
        @Test
        @DisplayName("판매한 작품을 조회한다")
        void success() {
        }
    }

    @Nested
    @DisplayName("구매한 작품 조회 Query")
    class FetchPurchaseArtsByType {
        @Test
        @DisplayName("구매한 작품을 조회한다")
        void success() {
        }
    }
}
