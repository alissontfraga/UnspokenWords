package com.alissontfraga.unspokenwords.security;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alissontfraga.unspokenwords.entity.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtil {

    public static final String ROLES_CLAIM = "roles";

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms}")
    private Long expiration;

    private Algorithm algorithm;

    @PostConstruct
    public void init() {
        this.algorithm = Algorithm.HMAC256(jwtSecret);
    }

    public String generateToken(User user) {
        Instant now = Instant.now();

        return JWT.create()
                .withSubject(user.getUsername())
                .withClaim(
                        ROLES_CLAIM,
                        user.getRoles().stream()
                                .map(Enum::name)
                                .toList()
                )
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plusMillis(expiration)))
                .sign(algorithm);
    }

    public DecodedJWT decode(String token) {
        return JWT.require(algorithm)
                .build()
                .verify(token);
    }

    public String extractUsername(DecodedJWT decoded) {
        return decoded.getSubject();
    }

    public List<String> extractRoles(DecodedJWT decoded) {
        return Optional
                .ofNullable(decoded.getClaim(ROLES_CLAIM)
                        .asList(String.class))
                .orElse(List.of());
    }
}