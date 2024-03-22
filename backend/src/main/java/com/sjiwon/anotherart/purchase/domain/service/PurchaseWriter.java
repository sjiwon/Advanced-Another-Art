package com.sjiwon.anotherart.purchase.domain.service;

import com.sjiwon.anotherart.purchase.domain.model.Purchase;
import com.sjiwon.anotherart.purchase.domain.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PurchaseWriter {
    private final PurchaseRepository purchaseRepository;

    public Purchase save(final Purchase target) {
        return purchaseRepository.save(target);
    }
}
