package com.sjiwon.anotherart.favorite.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    // @Query
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM Favorite f WHERE f.artId = :artId AND f.memberId = :memberId")
    void deleteFavoriteMarking(@Param("artId") Long artId, @Param("memberId") Long memberId);

    // Query Method
    boolean existsByArtIdAndMemberId(Long artId, Long memberId);
}
