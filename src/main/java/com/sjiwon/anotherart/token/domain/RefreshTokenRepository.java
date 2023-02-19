package com.sjiwon.anotherart.token.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    // @Query
    @Modifying(clearAutomatically = true)
    @Query("UPDATE RefreshToken rt" +
            " SET rt.refreshToken = :newRefreshToken" +
            " WHERE rt.memberId = :memberId")
    void reissueRefreshTokenByRtrPolicy(@Param("memberId") Long memberId, @Param("newRefreshToken") String newRefreshToken);

    // Query Method
    boolean existsByMemberId(Long memberId);
    boolean existsByMemberIdAndRefreshToken(Long memberId, String refreshToken);
    void deleteByMemberId(Long memberId);
}
