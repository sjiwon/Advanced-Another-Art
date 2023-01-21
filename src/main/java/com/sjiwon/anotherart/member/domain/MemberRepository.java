package com.sjiwon.anotherart.member.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByNickname(String nickname);
    boolean existsByLoginId(String loginId);
    boolean existsByPhone(String phone);
    boolean existsByEmail(Email email);
}
