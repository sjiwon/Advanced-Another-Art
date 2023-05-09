package com.sjiwon.anotherart.art.domain;

import com.sjiwon.anotherart.art.infra.query.ArtDetailQueryRepository;
import com.sjiwon.anotherart.art.infra.query.ArtSingleQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ArtRepository extends JpaRepository<Art, Long>,
        ArtSingleQueryRepository, ArtDetailQueryRepository {
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

    // Query Method
    boolean existsByName(ArtName name);
    boolean existsByNameAndIdNot(ArtName name, Long artId);
    boolean existsByIdAndOwnerId(Long artId, Long memberId);
}
