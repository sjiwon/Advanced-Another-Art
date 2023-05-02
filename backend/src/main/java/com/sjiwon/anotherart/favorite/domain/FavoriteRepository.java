package com.sjiwon.anotherart.favorite.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    boolean existsByArtIdAndMemberId(Long artId, Long memberId);
    void deleteByArtIdAndMemberId(Long artId, Long memberId);
}
