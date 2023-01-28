package com.sjiwon.anotherart.global.security.filter;

import com.sjiwon.anotherart.global.security.exception.AnotherArtAccessDeniedException;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import com.sjiwon.anotherart.global.security.principal.MemberAuthDto;
import com.sjiwon.anotherart.global.security.principal.MemberPrincipal;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.token.utils.AuthorizationExtractor;
import com.sjiwon.anotherart.token.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = AuthorizationExtractor.extractToken(request);

        if (token != null) {
            if (jwtTokenProvider.isTokenValid(token)) {
                Long memberId = jwtTokenProvider.getPayload(token);
                Member member = memberRepository.findById(memberId)
                        .orElseThrow(() -> AnotherArtAccessDeniedException.type(AuthErrorCode.INVALID_TOKEN));
                MemberPrincipal principal = new MemberPrincipal(new MemberAuthDto(member));
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(principal, "");
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                throw AnotherArtAccessDeniedException.type(AuthErrorCode.INVALID_TOKEN);
            }
        }

        filterChain.doFilter(request, response);
    }
}
