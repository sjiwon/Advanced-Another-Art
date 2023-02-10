package com.sjiwon.anotherart.token.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "refresh_token")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false, updatable = false, unique = true)
    private Long memberId;

    @Column(name = "refresh_token", nullable = false, updatable = false, unique = true)
    private String refreshToken;

    @Builder
    private RefreshToken(Long memberId, String refreshToken) {
        this.memberId = memberId;
        this.refreshToken = refreshToken;
    }

    public static RefreshToken issueRefreshToken(Long memberId, String refreshToken) {
        return new RefreshToken(memberId, refreshToken);
    }
}
