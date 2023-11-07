package com.sjiwon.anotherart.art.domain.repository;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.model.ArtName;
import com.sjiwon.anotherart.art.domain.model.ArtType;
import com.sjiwon.anotherart.art.domain.repository.query.ArtDetailQueryRepository;
import com.sjiwon.anotherart.art.domain.repository.query.ArtSimpleQueryRepository;
import com.sjiwon.anotherart.art.domain.repository.query.ArtSingleQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ArtRepository extends JpaRepository<Art, Long>,
        ArtSingleQueryRepository, ArtDetailQueryRepository, ArtSimpleQueryRepository {
    // @Query
    @Query("SELECT a" +
            " FROM Art a" +
            " JOIN FETCH a.owner" +
            " WHERE a.id = :artId")
    Optional<Art> findByIdWithOwner(@Param("artId") Long artId);

    @Query("SELECT a.type" +
            " FROM Art a" +
            " WHERE a.id = :artId")
    ArtType getArtTypeById(@Param("artId") Long artId);

    @Query("SELECT a.owner.id" +
            " FROM Art a" +
            " WHERE a.id = :artId")
    Long getOwnerId(@Param("artId") final Long artId);

    default boolean isOwner(final Long artId, final Long memberId) {
        return getOwnerId(artId).equals(memberId);
    }

    // Query Method
    boolean existsByName(ArtName name);

    boolean existsByNameAndIdNot(ArtName name, Long artId);

    boolean existsByIdAndOwnerId(Long artId, Long memberId);
}
