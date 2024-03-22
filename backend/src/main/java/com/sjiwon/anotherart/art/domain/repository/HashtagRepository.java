package com.sjiwon.anotherart.art.domain.repository;

import com.sjiwon.anotherart.art.domain.model.Hashtag;
import com.sjiwon.anotherart.global.annotation.AnotherArtWritableTransactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
    @AnotherArtWritableTransactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM Hashtag h WHERE h.art.id = :artId")
    void deleteArtHashtags(@Param("artId") Long artId);
}
