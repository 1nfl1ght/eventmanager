package com.course.eventnotificator.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtTokenManager {

    private final SecretKey secretKey;

    public JwtTokenManager(
            @Value("${jwt.secret}") String keyString
    ) {
        this.secretKey = Keys.hmacShaKeyFor(keyString.getBytes());
    }

    public String getLoginFromToken(String token) {
        return Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

}
