package aifriend.ai_backend.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // Default to 24 hours
    private long expirationTime;

    /**
     * Generate a JWT token for a user
     */
    public String generateToken(Long userId, String email) {
        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withClaim("email", email)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .sign(Algorithm.HMAC512(secret.getBytes()));
    }

    /**
     * Validate a JWT token
     */
    public DecodedJWT validateToken(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC512(secret.getBytes()))
                .build();
        return verifier.verify(token);
    }

    /**
     * Extract user ID from a JWT token
     */
    public Long getUserIdFromToken(String token) {
        DecodedJWT jwt = validateToken(token);
        return Long.parseLong(jwt.getSubject());
    }

    /**
     * Extract email from a JWT token
     */
    public String getEmailFromToken(String token) {
        DecodedJWT jwt = validateToken(token);
        return jwt.getClaim("email").asString();
    }

    /**
     * Check if a token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            DecodedJWT jwt = validateToken(token);
            return jwt.getExpiresAt().before(new Date());
        } catch (JWTVerificationException e) {
            return true;
        }
    }
}
