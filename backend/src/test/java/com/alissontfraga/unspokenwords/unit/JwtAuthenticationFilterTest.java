package com.alissontfraga.unspokenwords.unit;

import com.alissontfraga.unspokenwords.security.JwtAuthenticationFilter;
import com.alissontfraga.unspokenwords.security.JwtUtil;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private final JwtUtil jwtUtil = mock(JwtUtil.class);
    private final JwtAuthenticationFilter filter =
            new JwtAuthenticationFilter(jwtUtil);

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }


    @Test
    void shouldAuthenticateWithValidToken() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/messages");
        request.setCookies(new Cookie("token", "valid"));

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        DecodedJWT decoded = mock(DecodedJWT.class);

        when(jwtUtil.decode("valid")).thenReturn(decoded);
        when(jwtUtil.extractUsername(decoded)).thenReturn("user");
        when(jwtUtil.extractRoles(decoded)).thenReturn(List.of("ROLE_USER"));

        filter.doFilter(request, response, chain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("user",
                SecurityContextHolder.getContext().getAuthentication().getName());

        verify(chain).doFilter(request, response);
    }


    @Test
    void shouldIgnoreInvalidToken() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/messages");
        request.setCookies(new Cookie("token", "invalid"));

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        when(jwtUtil.decode("invalid"))
                .thenThrow(new JWTVerificationException("invalid"));

        filter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }

    // PARAMETERIZED (NO TOKEN OR IGNORED PATHS)

    @ParameterizedTest
    @ValueSource(strings = {
            "/api/messages",          // sem token
            "/api/auth/login",        // ignorado
            "/swagger-ui/index.html", // ignorado
            "/v3/api-docs"            // ignorado
    })
    void shouldNotAuthenticateForIgnoredOrNoTokenPaths(String path) throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath(path);

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }


    @Test
    void shouldNotAuthenticateIfAlreadyAuthenticated() throws Exception {

        Authentication existingAuth = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(existingAuth);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/messages");
        request.setCookies(new Cookie("token", "valid"));

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertEquals(existingAuth,
                SecurityContextHolder.getContext().getAuthentication());

        verify(chain).doFilter(request, response);
    }

    @Test
    void shouldExtractTokenFromMultipleCookies() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/messages");
        request.setCookies(
                new Cookie("other", "123"),
                new Cookie("token", "valid")
        );

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        DecodedJWT decoded = mock(DecodedJWT.class);

        when(jwtUtil.decode("valid")).thenReturn(decoded);
        when(jwtUtil.extractUsername(decoded)).thenReturn("user");
        when(jwtUtil.extractRoles(decoded)).thenReturn(List.of("ROLE_USER"));

        filter.doFilter(request, response, chain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldReturnNullWhenCookiesAreNull() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/messages");
        request.setCookies((Cookie[]) null);

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }

    // COOKIES EXISTS BUT WITHOUT TOKEN 
    @Test
    void shouldReturnNullWhenTokenCookieIsNotPresent() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/messages");
        request.setCookies(
                new Cookie("other1", "123"),
                new Cookie("other2", "456")
        );

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }

}
