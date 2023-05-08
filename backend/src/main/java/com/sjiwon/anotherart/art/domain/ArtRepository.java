package com.sjiwon.anotherart.art.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ArtRepository extends JpaRepository<Art, Long> {
    // @Query
    @Query("SELECT a" +
            " FROM Art a" +
            " JOIN FETCH a.owner" +
            " WHERE a.id = :artId")
    Optional<Art> findByIdWithOwner(@Param("artId") Long artId);

    // Query Method
    boolean existsByName(ArtName name);
    boolean existsByNameAndIdNot(ArtName name, Long artId);
}
