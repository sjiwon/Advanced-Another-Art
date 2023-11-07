package com.sjiwon.anotherart.auction.domain;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
    @Override
    @Query("SELECT ac" +
            " FROM Auction ac" +
            " JOIN FETCH ac.art a" +
            " JOIN FETCH a.owner" +
            " WHERE ac.id = :auctionId")
    Optional<Auction> findById(@Param("auctionId") Long auctionId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ac" +
            " FROM Auction ac" +
            " JOIN FETCH ac.art a" +
            " JOIN FETCH a.owner" +
            " WHERE ac.id = :auctionId")
    Optional<Auction> findByIdWithPessimisticLock(@Param("auctionId") Long auctionId);

    @Query("SELECT ac" +
            " FROM Auction ac" +
            " JOIN FETCH ac.art a" +
            " JOIN FETCH a.owner" +
            " WHERE a.id = :artId")
    Optional<Auction> findByArtId(@Param("artId") Long artId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM Auction ac WHERE ac.art.id = :artId")
    void deleteByArtId(@Param("artId") Long artId);

    @Query("SELECT ac.bidders.highestBidder.id" +
            " FROM Auction ac" +
            " WHERE ac.art.id = :artId")
    Long getBidderIdByArtId(@Param("artId") Long artId);

    default boolean isBidRecordExists(final Long artId) {
        return getBidderIdByArtId(artId) != null;
    }
}
