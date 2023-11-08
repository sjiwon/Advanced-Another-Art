package com.sjiwon.anotherart.auction.domain.repository;

import com.sjiwon.anotherart.auction.domain.model.AuctionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionRecordRepository extends JpaRepository<AuctionRecord, Long> {
}
