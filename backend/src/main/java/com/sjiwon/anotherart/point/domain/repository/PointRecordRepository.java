package com.sjiwon.anotherart.point.domain.repository;

import com.sjiwon.anotherart.point.domain.model.PointRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointRecordRepository extends JpaRepository<PointRecord, Long> {
}
