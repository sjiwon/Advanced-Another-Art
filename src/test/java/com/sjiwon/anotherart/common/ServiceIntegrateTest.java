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
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestPropertySource(properties = "file.dir=src/test/resources/images/storage/")
public abstract class ServiceIntegrateTest extends RedisTestContainers {
    @Autowired
    protected MemberRepository memberRepository;
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

    @AfterEach
    void after() {
        redisTokenRepository.deleteAll();
    }
}
