package com.sjiwon.anotherart.member.domain.repository;

import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    default Member getById(final Long id) {
        return findById(id)
                .orElseThrow(() -> AnotherArtException.type(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    // @Query
    @Query("SELECT m" +
            " FROM Member m" +
            " WHERE m.name = :name AND m.email.value = :email")
    Optional<Member> findByNameAndEmail(@Param("name") final String name, @Param("email") final String email);

    default Member getByNameAndEmail(final String name, final String email) {
        return findByNameAndEmail(name, email)
                .orElseThrow(() -> AnotherArtException.type(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    @Query("SELECT m" +
            " FROM Member m" +
            " WHERE m.name = :name AND m.email.value = :email AND m.loginId = :loginId")
    Optional<Member> findByNameAndEmailAndLoginId(
            @Param("name") final String name,
            @Param("email") final String email,
            @Param("loginId") final String loginId
    );

    default Member getByNameAndEmailAndLoginId(final String name, final String email, final String loginId) {
        return findByNameAndEmailAndLoginId(name, email, loginId)
                .orElseThrow(() -> AnotherArtException.type(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    @Query("SELECT m.id" +
            " FROM Member m" +
            " WHERE m.nickname.value = :nickname")
    Long findIdByNicknameUsed(@Param("nickname") final String nickname);

    default boolean isNicknameUsedByOther(final Long memberId, final String nickname) {
        final Long nicknameUsedId = findIdByNicknameUsed(nickname);
        return nicknameUsedId != null && !nicknameUsedId.equals(memberId);
    }

    // Query Method
    Optional<Member> findByLoginId(final String loginId);

    default Member getByLoginId(final String loginId) {
        return findByLoginId(loginId)
                .orElseThrow(() -> AnotherArtException.type(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    boolean existsByNicknameValue(final String nickname);

    boolean existsByLoginId(final String loginId);

    boolean existsByPhoneValue(final String phone);

    boolean existsByEmailValue(final String email);

    boolean existsByNameAndEmailValueAndLoginId(final String name, final String email, final String loginId);
}
