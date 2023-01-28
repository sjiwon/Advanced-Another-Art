package com.sjiwon.anotherart.token.domain;

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
    private String refreshToken;

    private Long memberId;

    @TimeToLive
    private long timeToLive = DEFAULT_TTL;

    @Builder
    public RedisRefreshToken(String refreshToken, Long memberId) {
        this.refreshToken = refreshToken;
        this.memberId = memberId;
    }
}
