package com.sjiwon.anotherart.common;

import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.art.domain.hashtag.HashtagRepository;
import com.sjiwon.anotherart.auction.domain.AuctionRepository;
import com.sjiwon.anotherart.auction.domain.record.AuctionRecordRepository;
import com.sjiwon.anotherart.favorite.domain.FavoriteRepository;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.member.domain.point.PointDetailRepository;
import com.sjiwon.anotherart.purchase.domain.PurchaseRepository;
import com.sjiwon.anotherart.token.domain.RedisTokenRepository;
import com.sjiwon.anotherart.token.service.RedisTokenService;
import com.sjiwon.anotherart.token.utils.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public abstract class ControllerTest extends RedisTestContainers {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected MemberRepository memberRepository;
    @Autowired
    protected JwtTokenProvider jwtTokenProvider;
    @Autowired
    protected PointDetailRepository pointDetailRepository;
    @Autowired
    protected ArtRepository artRepository;
    @Autowired
    protected HashtagRepository hashtagRepository;
    @Autowired
    protected FavoriteRepository favoriteRepository;
    @Autowired
    protected AuctionRepository auctionRepository;
    @Autowired
    protected AuctionRecordRepository auctionRecordRepository;
    @Autowired
    protected PurchaseRepository purchaseRepository;
    @Autowired
    protected RedisTokenRepository redisTokenRepository;
    @Autowired
    protected RedisTokenService redisTokenService;
}
