package com.sjiwon.anotherart.purchase.service;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.art.domain.hashtag.HashtagRepository;
import com.sjiwon.anotherart.common.utils.PasswordEncoderUtils;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.member.domain.*;
import com.sjiwon.anotherart.member.domain.point.PointDetail;
import com.sjiwon.anotherart.member.domain.point.PointDetailRepository;
import com.sjiwon.anotherart.member.domain.point.PointType;
import com.sjiwon.anotherart.purchase.domain.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.sjiwon.anotherart.common.utils.ArtUtils.HASHTAGS;
import static com.sjiwon.anotherart.common.utils.MemberUtils.INIT_AVAILABLE_POINT;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
@DisplayName("Purchase [Purchase Concurrency Test] -> 작품 구매 동시성 테스트")
class PurchaseServiceTest {
    private final PurchaseService purchaseService;
    private final MemberRepository memberRepository;
    private final PointDetailRepository pointDetailRepository;
    private final ArtRepository artRepository;
    private final HashtagRepository hashtagRepository;
    private final PurchaseRepository purchaseRepository;

    private static final MemberFixture MEMBER = MemberFixture.A;
    private final List<Long> participateMemberIdList = new ArrayList<>();

    @BeforeEach
    void before() {
        createParticipateMembers(); // 100명의 더미 사용자 생성
    }

    @AfterEach
    void after() {
        purchaseRepository.deleteAll();
        hashtagRepository.deleteAll();
        artRepository.deleteAll();
        pointDetailRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("100명의 사용자가 동일한 일반 작품을 동시에 구매하려고 시도하면 구매 내역에는 1건의 내역만 존재해야 한다")
    void test() throws Exception {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        // given
        Member owner = createArtOwner();
        Art generalArt = createGeneralArt(owner);

        // when
        for (Long participateMemberId : participateMemberIdList) {
            executorService.submit(() -> {
                try {
                    purchaseService.purchaseArt(generalArt.getId(), participateMemberId);
                } catch (Exception e) {
                    log.error(e.getMessage());
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        // then
        assertThat(purchaseRepository.findAll().size()).isEqualTo(1);
    }

    private void createParticipateMembers() {
        List<Member> members = new ArrayList<>();
        List<PointDetail> pointDetails = new ArrayList<>();

        for (int i = 1; i <= 100; i++) {
            Member member = Member.builder()
                    .name("Member" + i)
                    .nickname("Member" + i)
                    .loginId("member" + i)
                    .password(Password.encrypt(MEMBER.getPassword(), PasswordEncoderUtils.getEncoder()))
                    .school("경기대학교")
                    .address(Address.of(MEMBER.getPostcode(), MEMBER.getDefaultAddress(), MEMBER.getDetailAddress()))
                    .phone(generateRandomPhoneNumber())
                    .email(Email.from(generateRandomEmail()))
                    .build();
            members.add(member);
            pointDetails.add(PointDetail.insertPointDetail(member, PointType.CHARGE, INIT_AVAILABLE_POINT));
        }

        List<Member> savedMembers = memberRepository.saveAll(members);
        pointDetailRepository.saveAll(pointDetails);

        // ID(PK) 추출
        savedMembers.forEach(savedMember -> participateMemberIdList.add(savedMember.getId()));
    }

    private static String generateRandomPhoneNumber() {
        String result = "010";
        result += String.valueOf((int) (Math.random() * 9000 + 1000));
        result += String.valueOf((int) (Math.random() * 9000 + 1000));
        return result;
    }

    private static String generateRandomEmail() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10) + "@gmail.com";
    }

    private Member createArtOwner() {
        return memberRepository.save(MEMBER.toMember());
    }

    private Art createGeneralArt(Member owner) {
        return artRepository.save(ArtFixture.B.toArt(owner, HASHTAGS));
    }
}
