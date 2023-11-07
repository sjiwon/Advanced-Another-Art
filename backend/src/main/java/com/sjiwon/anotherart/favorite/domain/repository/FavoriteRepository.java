package com.sjiwon.anotherart.favorite.domain.repository;

import com.sjiwon.anotherart.favorite.domain.model.Favorite;
import com.sjiwon.anotherart.global.annotation.AnotherArtWritableTransactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    // @Query
    @AnotherArtWritableTransactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM Favorite f WHERE f.art.id = :artId")
    void deleteByArtId(@Param("artId") final Long artId);

    // Query Method
    Optional<Favorite> findByArtIdAndMemberId(final Long artId, final Long memberId);
}
