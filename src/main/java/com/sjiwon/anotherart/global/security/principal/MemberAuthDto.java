package com.sjiwon.anotherart.global.security.principal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sjiwon.anotherart.member.domain.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberAuthDto {
    private Long id;
    private String name;
    private String nickname;
    private String loginId;
    @JsonIgnore
    private String loginPassword;
    private String role;

    public MemberAuthDto(Member member) {
        this.id = member.getId();
        this.name = member.getName();
        this.nickname = member.getNickname();
        this.loginId = member.getLoginId();
        this.loginPassword = member.getPasswordValue();
        this.role = member.getRole().getAuthority();
    }
}
