package com.sjiwon.anotherart.art.domain;

import com.sjiwon.anotherart.art.infra.query.ArtSpecificSimpleQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArtRepository extends JpaRepository<Art, Long>, ArtSpecificSimpleQueryRepository {
    // @Query
    @Query("SELECT a" +
            " FROM Art a" +
            " WHERE a.owner.id = :ownerId")
    List<Art> findByOwnerId(@Param("ownerId") Long ownerId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Hashtag h WHERE h.art.id = :artId")
    void deleteHashtagsByArtId(@Param("artId") Long artId);

    @Query("SELECT a.artType" +
            " FROM Art a" +
            " WHERE a.id = :artId")
    ArtType searchArtType(@Param("artId") Long artId);

    boolean existsByIdAndOwnerId(Long artId, Long ownerId);

    // Query Method
    boolean existsByName(String artName);
}
