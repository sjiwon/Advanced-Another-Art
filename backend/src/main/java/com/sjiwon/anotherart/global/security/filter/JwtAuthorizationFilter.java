package com.sjiwon.anotherart.global.security.filter;

import com.sjiwon.anotherart.global.security.exception.AnotherArtAccessDeniedException;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import com.sjiwon.anotherart.global.security.principal.MemberPrincipal;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import com.sjiwon.anotherart.token.exception.InvalidTokenException;
import com.sjiwon.anotherart.token.utils.RequestTokenExtractor;
import com.sjiwon.anotherart.token.utils.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
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
        final Optional<String> token = RequestTokenExtractor.extractAccessToken(request);

        if (token.isPresent()) {
            try {
                final String accessToken = token.get();
                tokenProvider.validateToken(accessToken);

                final Member member = getMemberViaToken(accessToken);
                applyMemberToSecurityContext(member);
            } catch (final InvalidTokenException e) {
                throw AnotherArtAccessDeniedException.type(AuthErrorCode.INVALID_TOKEN);
            }
        }

        filterChain.doFilter(request, response);
    }

    private Member getMemberViaToken(final String accessToken) {
        return memberRepository.findById(tokenProvider.getId(accessToken))
                .orElseThrow(() -> AnotherArtAccessDeniedException.type(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    private void applyMemberToSecurityContext(final Member member) {
        final MemberPrincipal principal = new MemberPrincipal(member);
        final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
