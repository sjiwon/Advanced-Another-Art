package com.sjiwon.anotherart.member.infra.query;

public interface MemberPointQueryRepository {
    Integer getTotalPointsByMemberId(Long memberId);
}
