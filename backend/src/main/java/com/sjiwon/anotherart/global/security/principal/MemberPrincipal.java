package com.sjiwon.anotherart.global.security.principal;

import com.sjiwon.anotherart.member.domain.model.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public record MemberPrincipal(
        Long id,
        String name,
        String nickname,
        String loginId,
        String password,
        String authority
) implements UserDetails {
    public MemberPrincipal(final Member member) {
        this(
                member.getId(),
                member.getName(),
                member.getNickname().getValue(),
                member.getLoginId(),
                member.getPassword().getValue(),
                member.getRole().getAuthority()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(authority));
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return loginId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
