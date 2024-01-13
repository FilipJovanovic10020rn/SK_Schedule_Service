package com.example.scheduleservice.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class TokenService {

    // kljuc se salje sa drugih servisa
    private SecretKey jwtSecretKey = null ;

    public TokenService() {
        // Your original shared secret key

    }

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

    public void setJwtSecretKey(String encodedKey) {
        this.jwtSecretKey = reconstructSecretKey(encodedKey);
//        this.jwtSecretKey = jwtSecretKey;
    }

    private static SecretKey reconstructSecretKey(String encodedKey) {
        // Dekodirajte base64 string u bajtni niz
        byte[] keyBytes = Base64.getDecoder().decode(encodedKey);

        // Ponovno konstruirajte SecretKey iz bajtnog niza
        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

}
