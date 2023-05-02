package com.sjiwon.anotherart.member.domain;

import com.sjiwon.anotherart.member.infra.query.MemberPointQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberPointQueryRepository {
    Optional<Member> findByLoginId(String loginId);
    Optional<Member> findByNameAndEmail(String name, Email email);
    boolean existsByNickname(String nickname);
    boolean existsByLoginId(String loginId);
    boolean existsByPhone(String phone);
    boolean existsByEmail(Email email);
    boolean existsByNameAndLoginIdAndEmail(String name, String loginId, Email email);
}
