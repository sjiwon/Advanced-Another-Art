package com.sjiwon.anotherart.art.domain.hashtag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
    @Modifying
    @Query("DELETE FROM Hashtag h WHERE h.art.id = :artId")
    void deleteByArtId(@Param("artId") Long artId);
}
