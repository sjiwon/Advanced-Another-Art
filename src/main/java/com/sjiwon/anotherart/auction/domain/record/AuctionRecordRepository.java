package com.sjiwon.anotherart.auction.domain.record;

import com.sjiwon.anotherart.auction.infra.AuctionRecordQueryDslRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuctionRecordRepository extends JpaRepository<AuctionRecord, Long>, AuctionRecordQueryDslRepository {
    // @Query
    @Query("SELECT ar" +
            " FROM AuctionRecord ar" +
            " WHERE ar.auction.id = :auctionId")
    List<AuctionRecord> findByAuctionId(@Param("auctionId") Long auctionId);

    // Query Method
    long countByAuctionId(Long auctionId);
}
