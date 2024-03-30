package com.sjiwon.anotherart.auction.domain.repository;

import com.sjiwon.anotherart.auction.domain.model.Auction;
import com.sjiwon.anotherart.global.annotation.AnotherArtWritableTransactional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
    // @Query
    @Query("""
            SELECT ac
            FROM Auction ac
            WHERE ac.id = :id
            """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Auction> findByIdWithLock(@Param("id") Long id);

    @Query("""
            SELECT ac.id
            FROM Auction ac
            WHERE ac.artId = :artId
            """)
    Long findIdByArtId(@Param("artId") Long artId);

    @Query("""
            SELECT ac.highestBidderId
            FROM Auction ac
            WHERE ac.artId = :artId
            """)
    Long findHighestBidderIdByArtId(@Param("artId") Long artId);

    @AnotherArtWritableTransactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM Auction ac WHERE ac.id = :id")
    void deleteAuction(@Param("id") Long id);

    // Query Method
    Optional<Auction> findByArtId(final long artId);
}
