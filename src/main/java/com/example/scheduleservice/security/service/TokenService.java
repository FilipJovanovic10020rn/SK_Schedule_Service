package com.example.scheduleservice.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Service
public class TokenService {


    // Postavite svoj zajednički tajni ključ ovdje
    private final String sharedSecretKey = "tokenKey";
    private final SecretKey jwtSecretKey = Keys.hmacShaKeyFor(sharedSecretKey.getBytes());
//    private SecretKey jwtSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    public Claims parseToken(String jwt) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(jwtSecretKey)
                    .parseClaimsJws(jwt)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
        return claims;
    }
}
