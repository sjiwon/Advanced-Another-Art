package com.sjiwon.anotherart.favorite.domain;

import com.sjiwon.anotherart.global.annotation.AnotherArtWritableTransactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    // @Query
    @AnotherArtWritableTransactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM Favorite f WHERE f.artId = :artId AND f.memberId = :memberId")
    void deleteFavoriteMarking(@Param("artId") final Long artId, @Param("memberId") final Long memberId);

    @AnotherArtWritableTransactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM Favorite f WHERE f.artId = :artId")
    void deleteByArtId(@Param("artId") final Long artId);

    // Query Method
    boolean existsByArtIdAndMemberId(final Long artId, final Long memberId);
}
