package com.sjiwon.anotherart.auction.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
    @Query("SELECT ac" +
            " FROM Auction ac" +
            " WHERE ac.art.id = :artId")
    Auction findByArtId(@Param("artId") Long artId);

    @Query("SELECT ac" +
            " FROM Auction ac" +
            " LEFT OUTER JOIN FETCH ac.currentHighestBidder.bidder" +
            " WHERE ac.id = :auctionId")
    Optional<Auction> findByIdWithHighestBidder(@Param("auctionId") Long auctionId);
}
