package com.sjiwon.anotherart.token.domain.service;

import com.sjiwon.anotherart.token.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

@Component
public class TokenProvider {
    private static final String ISSUER = "AnotherArt";
    private static final String ACCESS_TOKEN_SUBJECT = "Auth";
    private static final String REFRESH_TOKEN_SUBJECT = "Reissue";

    private final SecretKey secretKey;
    private final long accessTokenValidityInSeconds;
    private final long refreshTokenValidityInSeconds;

    public TokenProvider(
            @Value("${jwt.secret-key}") final String secretKey,
            @Value("${jwt.access-token-validity}") final long accessTokenValidityInSeconds,
            @Value("${jwt.refresh-token-validity}") final long refreshTokenValidityInSeconds
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidityInSeconds = accessTokenValidityInSeconds;
        this.refreshTokenValidityInSeconds = refreshTokenValidityInSeconds;
    }

    public String createAccessToken(final Long memberId, final String authority) {
        // Payload
        final Claims claims = Jwts.claims();
        claims.put("id", memberId);
        claims.put("authority", authority);

        // Expire
        final ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        final ZonedDateTime tokenValidity = now.plusSeconds(accessTokenValidityInSeconds);

        return createToken(
                ACCESS_TOKEN_SUBJECT,
                claims,
                Date.from(now.toInstant()),
                Date.from(tokenValidity.toInstant())
        );
    }

    public String createRefreshToken(final Long memberId) {
        // Payload
        final Claims claims = Jwts.claims();
        claims.put("id", memberId);

        // Expire
        final ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        final ZonedDateTime tokenValidity = now.plusSeconds(refreshTokenValidityInSeconds);

        return createToken(
                REFRESH_TOKEN_SUBJECT,
                claims,
                Date.from(now.toInstant()),
                Date.from(tokenValidity.toInstant())
        );
    }

    private String createToken(
            final String subject,
            final Claims claims,
            final Date issuedAt,
            final Date expiration
    ) {
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(claims)
                .setIssuer(ISSUER)
                .setSubject(subject)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .setNotBefore(issuedAt)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .setId(UUID.randomUUID().toString())
                .compact();
    }

    public Long getId(final String token) {
        return getClaims(token)
                .getBody()
                .get("id", Long.class);
    }

    public void validateAccessToken(final String token) {
        validateToken(token, ACCESS_TOKEN_SUBJECT);
    }

    public void validateRefreshToken(final String token) {
        validateToken(token, REFRESH_TOKEN_SUBJECT);
    }

    public void validateToken(final String token, final String subject) {
        try {
            final Jws<Claims> claims = getClaims(token);
            final Claims payload = claims.getBody();
            checkExpiration(payload);
            checkIssuer(payload);
            checkSubject(payload, subject);
        } catch (final ExpiredJwtException |
                       SecurityException |
                       MalformedJwtException |
                       UnsupportedJwtException |
                       IllegalArgumentException e) {
            throw new InvalidTokenException();
        }
    }

    private void checkExpiration(final Claims payload) {
        final ZonedDateTime expiredDate = ZonedDateTime.ofInstant(payload.getExpiration().toInstant(), ZoneId.systemDefault());
        final ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        if (expiredDate.isBefore(now)) {
            throw new InvalidTokenException();
        }
    }

    private void checkIssuer(final Claims payload) {
        if (!ISSUER.equals(payload.getIssuer())) {
            throw new InvalidTokenException();
        }
    }

    private void checkSubject(final Claims payload, final String subject) {
        if (!subject.equals(payload.getSubject())) {
            throw new InvalidTokenException();
        }
    }

    private Jws<Claims> getClaims(final String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
    }
}
