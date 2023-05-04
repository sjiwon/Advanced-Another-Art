package com.sjiwon.anotherart.member.infra.query;

import com.sjiwon.anotherart.member.infra.query.dto.response.MemberPointRecord;

import java.util.List;

public interface MemberInformationQueryRepository {
    List<MemberPointRecord> findPointRecordByMemberId(Long memberId);
}
