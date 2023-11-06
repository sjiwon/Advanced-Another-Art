package com.sjiwon.anotherart.global.security.filter;

import com.sjiwon.anotherart.global.security.exception.AnotherArtAccessDeniedException;
import com.sjiwon.anotherart.global.security.principal.MemberPrincipal;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.model.Role;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import com.sjiwon.anotherart.token.exception.TokenErrorCode;
import com.sjiwon.anotherart.token.utils.AuthorizationExtractor;
import com.sjiwon.anotherart.token.utils.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain
    ) throws ServletException, IOException {
        final Optional<String> token = AuthorizationExtractor.extractToken(request);

        if (token.isPresent()) {
            if (tokenProvider.isTokenValid(token.get())) {
                final UsernamePasswordAuthenticationToken securityToken = buildSecurityToken(token.get());
                SecurityContextHolder.getContext().setAuthentication(securityToken);
            } else {
                throw AnotherArtAccessDeniedException.type(TokenErrorCode.INVALID_TOKEN);
            }
        }

        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken buildSecurityToken(final String token) {
        final Member member = memberRepository.findById(tokenProvider.getId(token))
                .orElseThrow(() -> AnotherArtAccessDeniedException.type(MemberErrorCode.MEMBER_NOT_FOUND));
        final MemberPrincipal principal = toMemberPrincipal(member);

        return new UsernamePasswordAuthenticationToken(principal, "", translateAuthorityRole(member.getRole()));
    }

    private MemberPrincipal toMemberPrincipal(final Member member) {
        return new MemberPrincipal(
                member.getId(),
                member.getName(),
                member.getNickname().getValue(),
                member.getLoginId(),
                member.getPassword().getValue(),
                member.getRole().getAuthority()
        );
    }

    private Collection<GrantedAuthority> translateAuthorityRole(final Role role) {
        final Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.getAuthority()));
        return authorities;
    }
}
