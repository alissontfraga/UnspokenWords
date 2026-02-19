package com.alissontfraga.unspokenwords.unit;

import com.alissontfraga.unspokenwords.controller.AuthController;
import com.alissontfraga.unspokenwords.dto.auth.*;
import com.alissontfraga.unspokenwords.entity.User;
import com.alissontfraga.unspokenwords.security.JwtUtil;
import com.alissontfraga.unspokenwords.service.UserService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User mockUser(Long id, String username, String password) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword(password);
        return user;
    }

    @Test
    void shouldRegisterSuccessfully() {
        RegisterRequest dto = new RegisterRequest("alice", "123");

        User user = mockUser(1L, "alice", "encoded");

        when(userService.createUser("alice", "123"))
                .thenReturn(user);

        ResponseEntity<RegisterResponse> response =
                authController.register(dto);

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertEquals("alice", response.getBody().username());
    }

    @Test
    void shouldReturnUnauthorizedWhenUserNotFound() {
        AuthRequest request = new AuthRequest("ghost", "123");

        when(userService.findByUsername("ghost"))
                .thenThrow(new UsernameNotFoundException("User not found"));

        MockHttpServletResponse servletResponse =
                new MockHttpServletResponse();

        ResponseEntity<AuthResponse> response =
                authController.login(request, servletResponse);

        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void shouldReturnUnauthorizedWhenPasswordInvalid() {
        AuthRequest request = new AuthRequest("alice", "wrong");

        User user = mockUser(1L, "alice", "encoded");

        when(userService.findByUsername("alice"))
                .thenReturn(user);
        when(passwordEncoder.matches("wrong", "encoded"))
                .thenReturn(false);

        MockHttpServletResponse servletResponse =
                new MockHttpServletResponse();

        ResponseEntity<AuthResponse> response =
                authController.login(request, servletResponse);

        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void shouldLoginAndSetHttpOnlyCookie() {
        AuthRequest request = new AuthRequest("alice", "123");

        User user = mockUser(1L, "alice", "encoded");

        when(userService.findByUsername("alice"))
                .thenReturn(user);
        when(passwordEncoder.matches("123", "encoded"))
                .thenReturn(true);
        when(jwtUtil.generateToken(user))
                .thenReturn("mockedToken");

        MockHttpServletResponse servletResponse =
                new MockHttpServletResponse();

        ResponseEntity<AuthResponse> response =
                authController.login(request, servletResponse);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("alice", response.getBody().username());

        String setCookieHeader =
                servletResponse.getHeader("Set-Cookie");

        assertNotNull(setCookieHeader);
        assertTrue(setCookieHeader.contains("token=mockedToken"));
        assertTrue(setCookieHeader.contains("HttpOnly"));
        assertTrue(setCookieHeader.contains("Max-Age=3600"));
    }

    @Test
    void shouldLogoutAndClearCookie() {
        MockHttpServletResponse servletResponse =
                new MockHttpServletResponse();

        ResponseEntity<Void> response =
                authController.logout(servletResponse);

        assertEquals(200, response.getStatusCode().value());

        String setCookieHeader =
                servletResponse.getHeader("Set-Cookie");

        assertNotNull(setCookieHeader);
        assertTrue(setCookieHeader.contains("token="));
        assertTrue(setCookieHeader.contains("Max-Age=0"));
        assertTrue(setCookieHeader.contains("HttpOnly"));
    }
}
