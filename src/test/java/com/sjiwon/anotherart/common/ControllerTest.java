package com.sjiwon.anotherart.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjiwon.anotherart.art.controller.ArtApiController;
import com.sjiwon.anotherart.art.controller.ArtDetailApiController;
import com.sjiwon.anotherart.art.controller.ArtSearchApiController;
import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.art.service.ArtFindService;
import com.sjiwon.anotherart.art.service.ArtSearchService;
import com.sjiwon.anotherart.art.service.ArtService;
import com.sjiwon.anotherart.auction.controller.BidApiController;
import com.sjiwon.anotherart.auction.service.AuctionFindService;
import com.sjiwon.anotherart.auction.service.BidService;
import com.sjiwon.anotherart.common.config.JwtTestConfiguration;
import com.sjiwon.anotherart.favorite.controller.FavoriteApiController;
import com.sjiwon.anotherart.favorite.service.FavoriteService;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.security.SecurityConfiguration;
import com.sjiwon.anotherart.member.controller.MemberApiController;
import com.sjiwon.anotherart.member.controller.MemberDetailApiController;
import com.sjiwon.anotherart.member.controller.MemberPointApiController;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.member.service.MemberFindService;
import com.sjiwon.anotherart.member.service.MemberPointService;
import com.sjiwon.anotherart.member.service.MemberService;
import com.sjiwon.anotherart.member.service.MemberValidator;
import com.sjiwon.anotherart.purchase.controller.PurchaseApiController;
import com.sjiwon.anotherart.purchase.service.PurchaseService;
import com.sjiwon.anotherart.token.controller.TokenReissueApiController;
import com.sjiwon.anotherart.token.service.TokenPersistenceService;
import com.sjiwon.anotherart.token.service.TokenReissueService;
import com.sjiwon.anotherart.token.utils.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.BDDMockito.given;

@WebMvcTest({
        ArtApiController.class,
        ArtDetailApiController.class,
        ArtSearchApiController.class,
        BidApiController.class,
        FavoriteApiController.class,
        MemberApiController.class,
        MemberDetailApiController.class,
        MemberPointApiController.class,
        PurchaseApiController.class,
        TokenReissueApiController.class
})
@Import({JwtTestConfiguration.class, SecurityConfiguration.class})
@AutoConfigureRestDocs
public abstract class ControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected JwtTokenProvider jwtTokenProvider;

    @MockBean
    protected ArtService artService;

    @MockBean
    protected ArtFindService artFindService;

    @MockBean
    protected ArtSearchService artSearchService;

    @MockBean
    protected AuctionFindService auctionFindService;

    @MockBean
    protected BidService bidService;

    @MockBean
    protected FavoriteService favoriteService;

    @MockBean
    protected MemberService memberService;

    @MockBean
    protected MemberFindService memberFindService;

    @MockBean
    protected MemberPointService memberPointService;

    @MockBean
    protected MemberValidator memberValidator;

    @MockBean
    protected PurchaseService purchaseService;

    @MockBean
    protected TokenReissueService tokenReissueService;

    @MockBean
    protected TokenPersistenceService tokenPersistenceService;

    @MockBean
    protected ArtRepository artRepository;

    @MockBean
    protected MemberRepository memberRepository;

    @BeforeEach
    void beforeMocking() {
        Member memberA = MemberFixture.A.toMember();
        Member memberB = MemberFixture.B.toMember();
        Member memberC = MemberFixture.C.toMember();

        given(memberRepository.findById(1L)).willReturn(Optional.ofNullable(memberA));
        given(memberRepository.findById(2L)).willReturn(Optional.ofNullable(memberB));
        given(memberRepository.findById(3L)).willReturn(Optional.ofNullable(memberC));

        given(memberRepository.findByLoginId(memberA.getLoginId())).willReturn(Optional.of(memberA));
        given(memberRepository.findByLoginId(memberB.getLoginId())).willReturn(Optional.of(memberB));
        given(memberRepository.findByLoginId(memberC.getLoginId())).willReturn(Optional.of(memberC));

        given(memberFindService.findById(1L)).willReturn(memberA);
        given(memberFindService.findById(2L)).willReturn(memberB);
        given(memberFindService.findById(3L)).willReturn(memberC);

        given(memberFindService.findByNameAndEmail(memberA.getName(), memberA.getEmail())).willReturn(memberA);
        given(memberFindService.findByNameAndEmail(memberB.getName(), memberB.getEmail())).willReturn(memberB);
        given(memberFindService.findByNameAndEmail(memberC.getName(), memberC.getEmail())).willReturn(memberC);
    }
}
