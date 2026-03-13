package com.arkea.sgesapi.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import io.jsonwebtoken.ExpiredJwtException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires — JwtService.
 */
class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Base64-encoded 256-bit key for HMAC-SHA256
        ReflectionTestUtils.setField(jwtService, "secret",
                "dGVzdC1zZWNyZXQta2V5LXRoYXQtaXMtbG9uZy1lbm91Z2gtZm9yLWhtYWMtc2hhLTI1Ng==");
        ReflectionTestUtils.setField(jwtService, "expiration", 3600000L); // 1 hour
    }

    @Test
    void generateToken_retourneTokenNonNull() {
        UserDetails user = createUser("admin");

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_retourneUsername() {
        UserDetails user = createUser("admin");
        String token = jwtService.generateToken(user);

        String username = jwtService.extractUsername(token);

        assertEquals("admin", username);
    }

    @Test
    void isTokenValid_tokenValide_retourneTrue() {
        UserDetails user = createUser("admin");
        String token = jwtService.generateToken(user);

        assertTrue(jwtService.isTokenValid(token, user));
    }

    @Test
    void isTokenValid_autreUser_retourneFalse() {
        UserDetails admin = createUser("admin");
        UserDetails other = createUser("other");
        String token = jwtService.generateToken(admin);

        assertFalse(jwtService.isTokenValid(token, other));
    }

    @Test
    void isTokenValid_tokenExpire_lanceException() {
        // Set expiration to 0ms (token expires immediately)
        ReflectionTestUtils.setField(jwtService, "expiration", 0L);

        UserDetails user = createUser("admin");
        String token = jwtService.generateToken(user);

        // Small delay to ensure expiration
        try { Thread.sleep(50); } catch (InterruptedException ignored) {}

        // jjwt throws ExpiredJwtException when parsing an expired token
        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(token, user));
    }

    @Test
    void generateToken_deuxTokens_differents() throws InterruptedException {
        UserDetails user = createUser("admin");

        String token1 = jwtService.generateToken(user);
        // JWT issuedAt has second-level resolution, need >1s delay
        Thread.sleep(1100);
        String token2 = jwtService.generateToken(user);

        assertNotEquals(token1, token2);
    }

    private UserDetails createUser(String username) {
        return User.builder()
                .username(username)
                .password("password")
                .authorities(Collections.emptyList())
                .build();
    }
}
