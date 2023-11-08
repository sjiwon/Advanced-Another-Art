package com.sjiwon.anotherart.purchase.domain.repository;

import com.sjiwon.anotherart.purchase.domain.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
}
