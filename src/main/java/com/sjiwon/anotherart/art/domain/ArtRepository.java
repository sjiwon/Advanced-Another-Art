package com.sjiwon.anotherart.art.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface ArtRepository extends JpaRepository<Art, Long> {
    // @Query
    @Query("SELECT a" +
            " FROM Art a" +
            " WHERE a.owner.id = :ownerId")
    List<Art> findByOwnerId(@Param("ownerId") Long ownerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a" +
            " FROM Art a" +
            " WHERE a.id = :artId")
    Optional<Art> findByIdWithPessimisticLock(@Param("artId") Long artId);

    @Modifying
    @Query("DELETE FROM Hashtag h WHERE h.art.id = :artId")
    void deleteHashtagsByArtId(@Param("artId") Long artId);

    // Query Method
    boolean existsByName(String artName);
}
