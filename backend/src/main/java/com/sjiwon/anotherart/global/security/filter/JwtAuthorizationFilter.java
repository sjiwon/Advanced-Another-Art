package com.sjiwon.anotherart.global.security.filter;

import com.sjiwon.anotherart.global.security.exception.AnotherArtAccessDeniedException;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import com.sjiwon.anotherart.global.security.principal.MemberPrincipal;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.member.domain.Role;
import com.sjiwon.anotherart.token.exception.TokenErrorCode;
import com.sjiwon.anotherart.token.utils.AuthorizationExtractor;
import com.sjiwon.anotherart.token.utils.JwtTokenProvider;
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
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        final Optional<String> token = AuthorizationExtractor.extractToken(request);

        if (token.isPresent()) {
            if (jwtTokenProvider.isTokenValid(token.get())) {
                final Long memberId = jwtTokenProvider.getId(token.get());
                final Member member = getMember(memberId);
                final MemberPrincipal principal = buildMemberPrincipal(member);

                final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        principal, "", translateMemberRole(member.getRole())
                );
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                throw AnotherArtAccessDeniedException.type(TokenErrorCode.AUTH_INVALID_TOKEN);
            }
        }

        filterChain.doFilter(request, response);
    }

    public Member getMember(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> AnotherArtAccessDeniedException.type(AuthErrorCode.INVALID_PERMISSION));
    }

    private MemberPrincipal buildMemberPrincipal(final Member member) {
        return new MemberPrincipal(
                member.getId(),
                member.getName(),
                member.getNicknameValue(),
                member.getLoginId(),
                member.getPasswordValue(),
                member.getRole().getAuthority()
        );
    }

    private Collection<GrantedAuthority> translateMemberRole(final Role role) {
        final Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.getAuthority()));
        return authorities;
    }
}
