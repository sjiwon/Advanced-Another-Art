package com.sjiwon.anotherart.global.redis;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.Duration;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "refreshToken")
public class RedisRefreshToken implements Serializable {
    private static final long DEFAULT_TTL = Duration.ofDays(14).toSeconds();

    @Id
    private Long memberId;

    private String refreshToken;

    @TimeToLive
    private long timeToLive = DEFAULT_TTL;

    @Builder
    public RedisRefreshToken(Long memberId, String refreshToken) {
        this.memberId = memberId;
        this.refreshToken = refreshToken;
    }
}
