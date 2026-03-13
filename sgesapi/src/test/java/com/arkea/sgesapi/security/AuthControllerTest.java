package com.arkea.sgesapi.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires — AuthController.
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthController authController;

    @Test
    void login_credentialsValides_retourneToken() {
        UserDetails user = User.builder()
                .username("admin")
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        when(userDetailsService.loadUserByUsername("admin")).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("jwt-token-123");

        AuthController.LoginRequest request = new AuthController.LoginRequest("admin", "password");
        ResponseEntity<Map<String, String>> response = authController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwt-token-123", response.getBody().get("token"));
        assertEquals("Bearer", response.getBody().get("type"));
        assertEquals("admin", response.getBody().get("username"));
    }

    @Test
    void login_credentialsInvalides_lanceException() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        AuthController.LoginRequest request = new AuthController.LoginRequest("admin", "wrong");

        assertThrows(BadCredentialsException.class, () -> authController.login(request));
    }
}
