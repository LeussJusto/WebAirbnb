package com.example.demo.config;

import com.example.demo.model.Role;
import com.example.demo.security.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    @DisplayName("generateJwtToken -> token valido y sujeto correcto")
    void generateAndValidateToken() throws Exception {
        JwtUtils jwtUtils = new JwtUtils();

        String secret = "01234567890123456789012345678901"; 
        setField(jwtUtils, "jwtSecret", secret);
        setField(jwtUtils, "jwtExpirationMs", 3600000L);

    UserDetailsImpl user = new UserDetailsImpl(1L, "Test User", "user@example.com", "pwd", java.util.Set.of(Role.USER));

        String token = jwtUtils.generateJwtToken(user);
        assertNotNull(token);

        assertTrue(jwtUtils.validateJwtToken(token), "El token generado debe validar");
        String subject = jwtUtils.getEmailFromJwtToken(token);
        assertEquals("user@example.com", subject);
    }

    @Test
    @DisplayName("validateJwtToken -> token expirado devuelve false")
    void expiredTokenShouldNotValidate() throws Exception {
        JwtUtils jwtUtils = new JwtUtils();

        String secret = "01234567890123456789012345678901"; // 32 chars
        setField(jwtUtils, "jwtSecret", secret);
        setField(jwtUtils, "jwtExpirationMs", 1000L);

        Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        String email = "expired@example.com";
        Date now = new Date();
        Date expired = new Date(now.getTime() - 1000L);

        String expiredToken = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expired)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        assertFalse(jwtUtils.validateJwtToken(expiredToken), "El token expirado no debe validar");
    }

    @Test
    @DisplayName("validateJwtToken -> token con firma inválida devuelve false")
    void invalidSignatureShouldNotValidate() throws Exception {
        JwtUtils jwtUtils = new JwtUtils();

        String secret = "01234567890123456789012345678901";
        setField(jwtUtils, "jwtSecret", secret);
        setField(jwtUtils, "jwtExpirationMs", 3600000L);

        String otherSecret = "abcdefghijklmnopqrstuvwxyz012345";
        Key otherKey = Keys.hmacShaKeyFor(otherSecret.getBytes(StandardCharsets.UTF_8));

        String email = "victim@example.com";
        Date now = new Date();

        String badToken = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + 3600000L))
                .signWith(otherKey, SignatureAlgorithm.HS256)
                .compact();

        assertFalse(jwtUtils.validateJwtToken(badToken), "El token con firma inválida no debe validar");
    }
}
