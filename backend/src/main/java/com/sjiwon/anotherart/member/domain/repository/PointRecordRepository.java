package com.sjiwon.anotherart.member.domain.repository;

import com.sjiwon.anotherart.member.domain.model.PointRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointRecordRepository extends JpaRepository<PointRecord, Long> {
}
