package com.sjiwon.anotherart.member.domain.repository;

import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.model.Email;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.model.Nickname;
import com.sjiwon.anotherart.member.domain.repository.query.MemberInformationQueryRepository;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberInformationQueryRepository {
    default Member getById(final Long id) {
        return findById(id)
                .orElseThrow(() -> AnotherArtException.type(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    Optional<Member> findByLoginId(String loginId);

    Optional<Member> findByNameAndEmail(String name, Email email);

    boolean existsByNickname(Nickname nickname);

    boolean existsByIdNotAndNickname(Long memberId, Nickname nickname);

    boolean existsByLoginId(String loginId);

    boolean existsByPhone(String phone);

    boolean existsByEmail(Email email);

    boolean existsByNameAndEmailAndLoginId(String name, Email email, String loginId);
}
