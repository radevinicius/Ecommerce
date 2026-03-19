package com.ecommerce.ecommerce.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class JwtService {



    private final String SECRET = "minha-chave-secreta";

    public String generateToken(Authentication authentication) {

        String email = authentication.getName();

        var roles = authentication.getAuthorities()
                .stream()
                .map(auth -> auth.getAuthority())
                .toList();

        return JWT.create()
                .withSubject(email)
                .withClaim("roles", roles)
                .withExpiresAt(expirationDate())
                .sign(Algorithm.HMAC256(SECRET));
    }


    public DecodedJWT getDecodedJWT(String token) {
        return JWT.require(Algorithm.HMAC256(SECRET))
                .build()
                .verify(token);
    }

    private Instant expirationDate() {
        return LocalDateTime.now()
                .plusHours(2)
                .toInstant(ZoneOffset.of("-03:00"));
    }

    public String validateToken(String token) {
        return JWT.require(Algorithm.HMAC256(SECRET))
                .build()
                .verify(token)
                .getSubject();
    }
}
