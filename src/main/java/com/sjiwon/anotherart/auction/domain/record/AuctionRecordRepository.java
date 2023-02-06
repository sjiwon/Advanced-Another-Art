package com.sjiwon.anotherart.auction.domain.record;

import com.sjiwon.anotherart.auction.infra.AuctionRecordQueryDslRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionRecordRepository extends JpaRepository<AuctionRecord, Long>, AuctionRecordQueryDslRepository {
    long countByAuctionId(Long auctionId);
}
