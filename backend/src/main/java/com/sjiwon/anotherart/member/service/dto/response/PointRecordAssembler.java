package com.sjiwon.anotherart.member.service.dto.response;

import com.sjiwon.anotherart.member.infra.query.dto.response.MemberPointRecord;

import java.util.List;

public record PointRecordAssembler(List<MemberPointRecord> result) {
}
