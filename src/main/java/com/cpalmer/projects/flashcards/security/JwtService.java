package com.cpalmer.projects.flashcards.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Service class responsible for creating, validating, and extracting information from JSON Web Tokens (JWTs).
 * This class handles all the cryptographic operations necessary for token-based authentication.
 *
 * @author cpalmer
 */
@Component
public class JwtService {

    // Secret key to verify the JWT token
    @Value("${security.jwt.secret-key}")
    private String jwtSecret;

    // The duration (in milliseconds) before the token expires
    @Value("${security.jwt.expiration-time}")
    private int jwtExpirationMs;

    /**
     * Extracts the username from the JWT token
     * Essentially, we cannot straight up access the username, rather it must be extracted from the token,
     * hence this method.
     *
     * @param token The JWT string.
     * @return The username
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Generates a new JWT token for a given user.
     * Contains the username, issed date, expiration take, and it's signed with the custom secret key
     *
     * @param username The username to set as the subject of the token.
     * @return The newly generated JWT string.
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigninKey(), SignatureAlgorithm.HS256) // Signs the token with the secret key using HS256 algorithm which I believe is the standard
                .compact();
    }

    /**
     * Checks if a token is valid for a given user.
     * Basically just tokens if the username matches and if the token hasn't expired yet.
     * See {@link #extractUsername(String)}
     *
     * @param token The JWT string.
     * @param userDetails The UserDetails to compare the token against.
     * @return True if the token is valid, false otherwise.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Converts the base64-encoded secret string into a security Key object.
     * This is used to verify the JWT token
     *
     * @return The secure Key object.
     */
    private Key getSigninKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Checks if the token's expiration date has passed.
     * Simple algorithm. Just checking if the expiration is before the current date.
     * See {@link #extractExpiration(String)}
     *
     * @param token The JWT string.
     * @return True if the expiration date is before the current date, false otherwise.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date claim from the token.
     *
     * @param token The JWT string.
     * @return The expiration Date object.
     */
    private Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    /**
     * Parses and validates the token signature, then extracts all the claims (payload data).
     * If the signature is invalid (token tampered with), this method will throw an exception.
     *
     * @param token The JWT string.
     * @return The Claims object containing all token information.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigninKey()) // Use the secret key to verify the signature
                .build()
                .parseClaimsJws(token) // Parses, verifies the signature, and validates the claims
                .getBody();
    }

}