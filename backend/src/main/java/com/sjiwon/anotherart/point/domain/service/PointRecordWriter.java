package com.sjiwon.anotherart.point.domain.service;

import com.sjiwon.anotherart.point.domain.model.PointRecord;
import com.sjiwon.anotherart.point.domain.repository.PointRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PointRecordWriter {
    private final PointRecordRepository pointRecordRepository;

    public PointRecord save(final PointRecord target) {
        return pointRecordRepository.save(target);
    }

    public List<PointRecord> save(final PointRecord... target) {
        return pointRecordRepository.saveAll(Arrays.asList(target));
    }
}
