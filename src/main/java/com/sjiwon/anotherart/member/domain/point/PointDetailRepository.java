package com.sjiwon.anotherart.member.domain.point;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PointDetailRepository extends JpaRepository<PointDetail, Long> {
    @Query("SELECT pd" +
            " FROM PointDetail pd" +
            " WHERE pd.member.id = :memberId")
    List<PointDetail> findByMemberId(@Param("memberId") Long memberId);
}
