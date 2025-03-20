// SupabaseTokenValidator.java
package lk.apollo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * The type Supabase token validator.
 */
@Component
public class SupabaseTokenValidator {
    private static final Logger logger = LoggerFactory.getLogger(SupabaseTokenValidator.class);
    private final JwtParser jwtParser;

    /**
     * Instantiates a new Supabase token validator.
     *
     * @param jwtSecret the jwt secret
     */
    public SupabaseTokenValidator(@Value("${supabase.jwt-secret}") String jwtSecret) {
        if (jwtSecret == null || jwtSecret.trim().isEmpty()) {
            logger.error("Supabase JWT secret must be provided and non-empty");
            throw new IllegalArgumentException("Supabase JWT secret must be provided and non-empty");
        }

        // Important: Supabase JWT secret handling
        // Supabase typically provides a JWT secret that should be used directly
        // as the signing key, not Base64 decoded first

        Key secretKey;
        try {
            // For Supabase, we should use the raw secret as is
            secretKey = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            logger.info("Using JWT secret key directly (Supabase standard)");
        } catch (Exception e) {
            logger.error("Failed to create secret key", e);
            throw new IllegalArgumentException("Failed to create proper secret key", e);
        }

        this.jwtParser = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build();
    }

    /**
     * Validate token and return claims.
     *
     * @param token the JWT token
     * @return the claims from the token
     * @throws JwtException if the token cannot be parsed or validated
     */
    public Claims validateToken(String token) throws JwtException {
        if (token == null || token.isEmpty()) {
            throw new JwtException("Token is null or empty");
        }

        // Remove 'Bearer ' prefix if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            logger.debug("Attempting to validate token");
            return jwtParser.parseClaimsJws(token).getBody();
        } catch (JwtException e) {
            logger.error("Token validation failed: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Determines if a token is valid.
     *
     * @param token the token
     * @return true if valid, false otherwise
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = validateToken(token);
            Date now = new Date();
            return claims.getExpiration() == null || !claims.getExpiration().before(now);
        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }
}