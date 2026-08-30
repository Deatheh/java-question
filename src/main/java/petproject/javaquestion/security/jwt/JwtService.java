package petproject.javaquestion.security.jwt;


import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import petproject.javaquestion.dto.TokenResponse;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Service
@Slf4j
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.access-expiration-ms}")
    private Long accessExpiration;

    @Value("${app.jwt.refresh-expiration-ms}")
    private Long refreshExpiration;

    public TokenResponse generateToken(String username){
        String accessToken = buildToken(username, accessExpiration);
        String refreshToken = buildToken(username, refreshExpiration);
        return new TokenResponse(accessToken, refreshToken);
    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractUsername(String token){
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public TokenResponse refreshToken(String refreshToken) {
        if (!validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }
        String username = extractUsername(refreshToken);
        return generateToken(username);
    }

    private SecretKey getSigningKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    private String buildToken(String username, Long expiration){
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expiration)))
                .signWith(getSigningKey())
                .compact();
    }
}
