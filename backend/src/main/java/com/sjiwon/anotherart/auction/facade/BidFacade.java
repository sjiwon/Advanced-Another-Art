package com.sjiwon.anotherart.auction.facade;

import com.sjiwon.anotherart.auction.service.BidService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class BidFacade {
    private final RedissonClient redissonClient;
    private final BidService bidService;

    public void bid(Long auctionId, Long bidderId, Integer bidPrice) {
        RLock rLock = redissonClient.getLock("ROCK_" + auctionId);

        try {
            boolean available = rLock.tryLock(5, 1, TimeUnit.SECONDS);

            if (!available) {
                return;
            }

            bidService.bid(auctionId, bidderId, bidPrice);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            rLock.unlock();
        }
    }
}
