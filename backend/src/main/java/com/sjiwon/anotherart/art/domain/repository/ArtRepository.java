package com.sjiwon.anotherart.art.domain.repository;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.global.annotation.AnotherArtWritableTransactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ArtRepository extends JpaRepository<Art, Long> {
    // @Query
    @Query("SELECT a.id" +
            " FROM Art a" +
            " WHERE a.name.value = :name")
    Long findIdByName(@Param("name") final String name);

    @AnotherArtWritableTransactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM Art a WHERE a.id = :id")
    void deleteArt(@Param("id") Long id);

    // Query Method
    Optional<Art> findByIdAndOwnerId(final Long id, final Long ownerId);

    boolean existsByNameValue(String name);
}
