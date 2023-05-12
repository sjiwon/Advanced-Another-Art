package com.sjiwon.anotherart.member.domain;

import com.sjiwon.anotherart.member.infra.query.MemberInformationQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>,
        MemberInformationQueryRepository {
    Optional<Member> findByLoginId(String loginId);
    Optional<Member> findByNameAndEmail(String name, Email email);
    boolean existsByNickname(Nickname nickname);
    boolean existsByIdNotAndNickname(Long memberId, Nickname nickname);
    boolean existsByLoginId(String loginId);
    boolean existsByPhone(String phone);
    boolean existsByEmail(Email email);
}
