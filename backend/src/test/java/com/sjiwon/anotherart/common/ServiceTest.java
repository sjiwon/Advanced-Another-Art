package com.sjiwon.anotherart.common;

import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.auction.domain.AuctionRepository;
import com.sjiwon.anotherart.common.config.TestContainerConfiguration;
import com.sjiwon.anotherart.favorite.domain.FavoriteRepository;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.purchase.domain.PurchaseRepository;
import com.sjiwon.anotherart.token.domain.TokenRepository;
import com.sjiwon.anotherart.token.utils.JwtTokenProvider;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ExtendWith(TestContainerConfiguration.class)
public class ServiceTest {
    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    protected EntityManager em;

    @Autowired
    protected JwtTokenProvider jwtTokenProvider;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected TokenRepository tokenRepository;

    @Autowired
    protected ArtRepository artRepository;

    @Autowired
    protected FavoriteRepository favoriteRepository;

    @Autowired
    protected AuctionRepository auctionRepository;

    @Autowired
    protected PurchaseRepository purchaseRepository;

    @AfterEach
    void clearDatabase() {
        databaseCleaner.cleanUpDatabase();
    }
}
