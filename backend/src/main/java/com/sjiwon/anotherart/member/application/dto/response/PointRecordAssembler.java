package com.sjiwon.anotherart.member.application.dto.response;

import com.sjiwon.anotherart.member.domain.repository.query.dto.response.MemberPointRecord;

import java.util.List;

public record PointRecordAssembler(List<MemberPointRecord> result) {
}
