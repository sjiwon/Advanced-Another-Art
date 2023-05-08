package com.sjiwon.anotherart.auction.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
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
}
