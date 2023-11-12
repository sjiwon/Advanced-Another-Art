package com.sjiwon.anotherart.auction.application.usecase;

import com.sjiwon.anotherart.auction.application.usecase.command.BidCommand;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class BidFacade {
    private final RedissonClient redissonClient;
    private final BidUseCase target;

    public void invoke(final BidCommand command) throws InterruptedException {
        final String key = "AUCTION:" + command.auctionId();
        final RLock lock = redissonClient.getLock(key);

        try {
            if (!tryLock(lock)) {
                return; // 획득 못하면 return -> 재시도 정책 추가 가능
            }

            target.invoke(command);
        } finally {
            unlock(lock);
        }
    }

    private boolean tryLock(final RLock lock) throws InterruptedException {
        return lock.tryLock(5, 1, TimeUnit.SECONDS);
    }

    private void unlock(final RLock lock) {
        if (lock.isLocked() && lock.isHeldByCurrentThread()) {
            lock.unlock();
        } else {
            throw new RuntimeException("anonymous try unlock or timeout...");
        }
    }
}
