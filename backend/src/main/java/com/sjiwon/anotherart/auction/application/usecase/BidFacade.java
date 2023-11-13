package com.sjiwon.anotherart.auction.application.usecase;

import com.sjiwon.anotherart.auction.application.usecase.command.BidCommand;
import com.sjiwon.anotherart.global.lock.LockManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Deprecated
@Component
@RequiredArgsConstructor
public class BidFacade {
    private final LockManager lockManager;
    private final BidUseCase target;

    @Transactional
    public void invoke(final BidCommand command) {
        final String key = "AUCTION:" + command.auctionId();
        final int timeout = 5;

        try {
            lockManager.acquire(key, timeout);
            target.invoke(command);
        } finally {
            lockManager.release(key);
        }
    }
}
