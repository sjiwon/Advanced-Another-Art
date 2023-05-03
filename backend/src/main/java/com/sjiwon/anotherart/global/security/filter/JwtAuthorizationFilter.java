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
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<String> token = AuthorizationExtractor.extractToken(request);

        if (token.isPresent()) {
            if (jwtTokenProvider.isTokenValid(token.get())) {
                Long memberId = jwtTokenProvider.getId(token.get());
                Member member = getMember(memberId);
                MemberPrincipal principal = buildMemberPrincipal(member);

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        principal, "", translateMemberRole(member.getRole())
                );
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                throw AnotherArtAccessDeniedException.type(TokenErrorCode.AUTH_INVALID_TOKEN);
            }
        }

        filterChain.doFilter(request, response);
    }

    public Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> AnotherArtAccessDeniedException.type(AuthErrorCode.INVALID_PERMISSION));
    }

    private MemberPrincipal buildMemberPrincipal(Member member) {
        return new MemberPrincipal(
                member.getId(),
                member.getName(),
                member.getNicknameValue(),
                member.getLoginId(),
                member.getPasswordValue(),
                member.getRole().getAuthority()
        );
    }

    private Collection<GrantedAuthority> translateMemberRole(Role role) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.getAuthority()));
        return authorities;
    }
}
