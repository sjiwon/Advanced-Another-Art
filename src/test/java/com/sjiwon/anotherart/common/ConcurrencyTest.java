package com.sjiwon.anotherart.common;

import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.art.domain.hashtag.HashtagRepository;
import com.sjiwon.anotherart.auction.domain.AuctionRepository;
import com.sjiwon.anotherart.auction.domain.record.AuctionRecordRepository;
import com.sjiwon.anotherart.favorite.domain.FavoriteRepository;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.member.domain.point.PointDetailRepository;
import com.sjiwon.anotherart.purchase.domain.PurchaseRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public abstract class ConcurrencyTest {
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

    @AfterEach
    void after() {
        purchaseRepository.deleteAll();
        auctionRecordRepository.deleteAll();
        auctionRepository.deleteAll();
        favoriteRepository.deleteAll();
        hashtagRepository.deleteAll();
        artRepository.deleteAll();
        pointDetailRepository.deleteAll();
        memberRepository.deleteAll();
    }
}
