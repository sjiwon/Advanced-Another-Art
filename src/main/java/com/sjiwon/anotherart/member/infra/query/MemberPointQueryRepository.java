package com.sjiwon.anotherart.member.infra.query;

import com.sjiwon.anotherart.member.infra.query.dto.response.UserPointHistory;

import java.util.List;

public interface MemberPointQueryRepository {
    Integer getTotalPointByMemberId(Long memberId);
    List<UserPointHistory> findUserPointHistoryByMemberId(Long memberId);
}
