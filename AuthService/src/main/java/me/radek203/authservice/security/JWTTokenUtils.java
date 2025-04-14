package me.radek203.authservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import me.radek203.authservice.exception.ResourceInvalidException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JWTTokenUtils {

    @Value("${app.jwt.key}")
    private String secretKey;

    @Value("${app.jwt.expiration}")
    private int expiration;

    public final String generateToken(final String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey(), Jwts.SIG.HS256)
                .compact();
    }

    public final boolean isTokenValid(final String token, final String username) {
        return getUsername(token).equals(username) && !isTokenExpired(token);
    }

    public final String getUsername(final String token) {
        return getClaims(token).getSubject();
    }

    private boolean isTokenExpired(final String token) {
        return getClaims(token).getExpiration().before(new Date(System.currentTimeMillis()));
    }

    private SecretKey getKey() {
        final byte[] key = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(key);
    }

    private Claims getClaims(final String token) {
        try {
            return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
        } catch (final ExpiredJwtException | SignatureException e) {
            throw new ResourceInvalidException("error/token-invalid", token);
        }
    }

}
