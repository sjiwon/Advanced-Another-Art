package com.sjiwon.anotherart.auction.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
    @Query("SELECT ac" +
            " FROM Auction ac" +
            " WHERE ac.art.id = :artId")
    Auction findByArtId(@Param("artId") Long artId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ac" +
            " FROM Auction ac" +
            " WHERE ac.id = :auctionId")
    Optional<Auction> findByIdWithPessimisticLock(@Param("auctionId") Long auctionId);
}
