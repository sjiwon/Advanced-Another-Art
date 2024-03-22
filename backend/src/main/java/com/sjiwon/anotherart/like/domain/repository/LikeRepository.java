package com.sjiwon.anotherart.like.domain.repository;

import com.sjiwon.anotherart.global.annotation.AnotherArtWritableTransactional;
import com.sjiwon.anotherart.like.domain.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    // @Query
    @AnotherArtWritableTransactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM Like l WHERE l.id = :id")
    void delete(@Param("id") final Long id);

    @AnotherArtWritableTransactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM Like l WHERE l.artId = :artId")
    void deleteByArtId(@Param("artId") final Long artId);

    // Query Method
    Optional<Like> findByArtIdAndMemberId(final Long artId, final Long memberId);
}
