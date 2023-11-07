package com.sjiwon.anotherart.art.domain.repository;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.model.ArtType;
import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ArtRepository extends JpaRepository<Art, Long> {
    default Art getById(final Long id) {
        return findById(id)
                .orElseThrow(() -> AnotherArtException.type(ArtErrorCode.ART_NOT_FOUND));
    }

    // @Query
    @Query("SELECT a" +
            " FROM Art a" +
            " JOIN FETCH a.owner" +
            " WHERE a.id = :id")
    Optional<Art> findByIdWitFetchOwner(@Param("id") Long id);

    default Art getByIdWithFetchOwner(final Long id) {
        return findByIdWitFetchOwner(id)
                .orElseThrow(() -> AnotherArtException.type(ArtErrorCode.ART_NOT_FOUND));
    }

    @Query("SELECT a.type" +
            " FROM Art a" +
            " WHERE a.id = :id")
    ArtType getArtTypeById(@Param("id") Long id);

    @Query("SELECT a.owner.id" +
            " FROM Art a" +
            " WHERE a.id = :id")
    Long getOwnerId(@Param("id") final Long id);

    default boolean isOwner(final Long artId, final Long memberId) {
        return getOwnerId(artId).equals(memberId);
    }

    @Query("SELECT a.id" +
            " FROM Art a" +
            " WHERE a.name.value = :name")
    Long findIdByNameUsed(@Param("name") final String name);

    default boolean isNameUsedByOther(final Long artId, final String name) {
        final Long nameUsedId = findIdByNameUsed(name);
        return nameUsedId != null && !nameUsedId.equals(artId);
    }

    // Query Method
    boolean existsByNameValue(String name);
}
