package com.sjiwon.anotherart.common;

import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.auction.domain.AuctionRepository;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.member.domain.point.PointRecordRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@SpringBootTest
public class ConcurrencyTest {
    @Autowired
    private DatabaseCleaner databaseCleaner;

    @PersistenceContext
    protected EntityManager em;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected PointRecordRepository pointRecordRepository;

    @Autowired
    protected ArtRepository artRepository;

    @Autowired
    protected AuctionRepository auctionRepository;

    @AfterEach
    void clearDatabase() {
        databaseCleaner.cleanUpDatabase();
    }
}
