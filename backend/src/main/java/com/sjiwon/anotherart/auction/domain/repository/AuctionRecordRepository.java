package com.sjiwon.anotherart.auction.domain.repository;

import com.sjiwon.anotherart.auction.domain.model.AuctionRecord;
import com.sjiwon.anotherart.global.annotation.AnotherArtWritableTransactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuctionRecordRepository extends JpaRepository<AuctionRecord, Long> {
    @AnotherArtWritableTransactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM AuctionRecord ar WHERE ar.auction.id = :auctionId")
    void deleteAuctionRecords(@Param("auctionId") Long auctionId);
}
