package com.sjiwon.anotherart.auction.domain.repository;

import com.sjiwon.anotherart.auction.domain.model.Auction;
import com.sjiwon.anotherart.auction.exception.AuctionErrorCode;
import com.sjiwon.anotherart.global.annotation.AnotherArtWritableTransactional;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
    @Query("SELECT ac" +
            " FROM Auction ac" +
            " LEFT JOIN FETCH ac.highestBid.bidder" +
            " WHERE ac.id = :id")
    Optional<Auction> findByIdWithFetchBidder(@Param("id") Long id);

    default Auction getByIdWithFetchBidder(final Long id) {
        return findByIdWithFetchBidder(id)
                .orElseThrow(() -> AnotherArtException.type(AuctionErrorCode.AUCTION_NOT_FOUND));
    }

    @Query("SELECT ac" +
            " FROM Auction ac" +
            " WHERE ac.art.id = :artId")
    Optional<Auction> findByArtId(@Param("artId") Long artId);

    default Auction getByArtIdWithFetchOwnerAndBidder(final Long artId) {
        return findByArtId(artId)
                .orElseThrow(() -> AnotherArtException.type(AuctionErrorCode.AUCTION_NOT_FOUND));
    }

    @AnotherArtWritableTransactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM Auction ac WHERE ac.art.id = :artId")
    void deleteByArtId(@Param("artId") Long artId);

    @Query("SELECT ac.highestBid.bidder.id" +
            " FROM Auction ac" +
            " WHERE ac.art.id = :artId")
    Long getBidderIdByArtId(@Param("artId") Long artId);

    default boolean isBidRecordExists(final Long artId) {
        return getBidderIdByArtId(artId) != null;
    }
}
