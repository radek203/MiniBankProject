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

    /**
     * Generates a JWT token for the given username.
     *
     * @param username the username to include in the token
     * @return the generated JWT token
     */
    public final String generateToken(final String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Validates the given JWT token for the specified username.
     *
     * @param token    the JWT token to validate
     * @param username the username to check against
     * @return true if the token is valid, false otherwise
     */
    public final boolean isTokenValid(final String token, final String username) {
        return getUsername(token).equals(username) && !isTokenExpired(token);
    }

    /**
     * Retrieves the username from the given JWT token.
     *
     * @param token the JWT token to extract the username from
     * @return the username extracted from the token
     */
    public final String getUsername(final String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Checks if the given JWT token is expired.
     *
     * @param token the JWT token to check
     * @return true if the token is expired, false otherwise
     */
    private boolean isTokenExpired(final String token) {
        return getClaims(token).getExpiration().before(new Date(System.currentTimeMillis()));
    }

    /**
     * Retrieves the secret key used for signing the JWT token.
     *
     * @return the secret key
     */
    private SecretKey getKey() {
        final byte[] key = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(key);
    }

    /**
     * Retrieves the claims from the given JWT token.
     *
     * @param token the JWT token to extract claims from
     * @return the claims extracted from the token
     */
    private Claims getClaims(final String token) {
        try {
            return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
        } catch (final ExpiredJwtException | SignatureException e) {
            throw new ResourceInvalidException("error/token-invalid", token);
        }
    }

}
