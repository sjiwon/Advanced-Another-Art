package com.sjiwon.anotherart.member.infra;

public interface MemberPointQueryDslRepository {
    Integer getTotalPointsByMemberId(Long memberId);
}
