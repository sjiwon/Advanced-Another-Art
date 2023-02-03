package com.sjiwon.anotherart.art.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArtRepository extends JpaRepository<Art, Long> {
    @Query("SELECT a" +
            " FROM Art a" +
            " WHERE a.owner.id = :ownerId")
    List<Art> findByOwnerId(@Param("ownerId") Long ownerId);
}
