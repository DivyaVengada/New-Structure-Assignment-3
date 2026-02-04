package com.company.payment.payment_backend.config;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;

import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    private <T>T extractClaims(String token, Function<Claims, T> claimResolver) {
        Claims claims = extractClaim(token);
        return claimResolver.apply(claims);
    }

    private Claims extractClaim(String token) {
        return  Jwts.parser().setSigningKey(secret).build().parseSignedClaims(token).getPayload();
    }
}
