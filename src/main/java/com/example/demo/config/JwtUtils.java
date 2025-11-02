package com.example.demo.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.example.demo.security.UserDetailsImpl;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import jakarta.annotation.PostConstruct;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.jwt.secret:${jwt.secret:default-secret-please-change}}")
    private String jwtSecret;

    @Value("${spring.jwt.expiration:${jwt.expiration:86400000}}")
    private long jwtExpirationMs;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    @PostConstruct
    public void validateSecretOnStartup() {
        if (jwtSecret == null || jwtSecret.isBlank()) {
            logger.error("JWT secret is not set. Application should not run with an empty secret.");
        } else if ("default-secret-please-change".equals(jwtSecret) || jwtSecret.length() < 32) {
            logger.warn("JWT secret appears weak or default (length={}): please set a strong secret via properties or environment variable.", jwtSecret.length());
        } else {
            logger.info("JWT secret loaded (length={})", jwtSecret.length());
        }
    }

    public String generateJwtToken(UserDetailsImpl userDetails) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getEmailFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // Logging estructurado, evita printStackTrace en tests/producción
            logger.debug("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }
}