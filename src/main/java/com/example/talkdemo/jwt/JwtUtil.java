package com.example.talkdemo.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

  
    private final long EXPIRATION = 1000 * 60 * 60 * 24; // 1일

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512); // 안전한 키 자동 생성


    public String generateToken(String nickname) {
        return Jwts.builder()
                .setSubject("user")
                .claim("nickname", nickname)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String validateAndGetUsername(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("nickname", String.class);
        } catch (JwtException e) {
            throw new IllegalArgumentException("JWT 유효성 검증 실패", e);
        }
    }
}
