package com.sjiwon.anotherart.purchase.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    @Query("SELECT p" +
            " FROM Purchase p" +
            " WHERE p.buyer.id = :buyerId")
    List<Purchase> findByBuyerId(@Param("buyerId") Long buyerId);
}
