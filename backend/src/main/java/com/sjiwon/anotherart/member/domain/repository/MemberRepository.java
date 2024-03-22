package com.sjiwon.anotherart.member.domain.repository;

import com.sjiwon.anotherart.member.domain.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    // @Query
    @Query("SELECT m" +
            " FROM Member m" +
            " WHERE m.name = :name AND m.email.value = :email")
    Optional<Member> findByNameAndEmail(@Param("name") final String name, @Param("email") final String email);

    @Query("SELECT m" +
            " FROM Member m" +
            " WHERE m.name = :name AND m.email.value = :email AND m.loginId = :loginId")
    Optional<Member> findByNameAndEmailAndLoginId(
            @Param("name") final String name,
            @Param("email") final String email,
            @Param("loginId") final String loginId
    );

    @Query("SELECT m.id" +
            " FROM Member m" +
            " WHERE m.nickname.value = :nickname")
    Long findIdByNickname(@Param("nickname") final String nickname);

    // Query Method
    Optional<Member> findByLoginId(final String loginId);

    boolean existsByNicknameValue(final String nickname);

    boolean existsByLoginId(final String loginId);

    boolean existsByPhoneValue(final String phone);

    boolean existsByEmailValue(final String email);
}
