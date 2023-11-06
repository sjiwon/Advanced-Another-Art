package com.sjiwon.anotherart.token.domain.repository;

import com.sjiwon.anotherart.global.annotation.AnotherArtWritableTransactional;
import com.sjiwon.anotherart.token.domain.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    // @Query
    @AnotherArtWritableTransactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Token t" +
            " SET t.refreshToken = :refreshToken" +
            " WHERE t.memberId = :memberId")
    void updateRefreshToken(@Param("memberId") Long memberId, @Param("refreshToken") String refreshToken);

    @AnotherArtWritableTransactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM Token t WHERE t.memberId = :memberId")
    void deleteRefreshToken(@Param("memberId") Long memberId);

    // Query Method
    Optional<Token> findByMemberId(Long memberId);

    boolean existsByMemberIdAndRefreshToken(Long memberId, String refreshToken);
}
