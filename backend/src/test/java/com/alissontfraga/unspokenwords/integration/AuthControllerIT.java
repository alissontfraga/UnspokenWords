package com.alissontfraga.unspokenwords.integration;

import com.alissontfraga.unspokenwords.dto.auth.AuthRequest;
import com.alissontfraga.unspokenwords.dto.auth.RegisterRequest;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.servlet.http.Cookie;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIT extends AbstractIntegrationTest {

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {

        String body = objectMapper.writeValueAsString(
                new RegisterRequest("newuser", "password123")
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void shouldReturnBadRequestWhenUsernameAlreadyExists() throws Exception {

        ensureUserExists("existing");

        String body = objectMapper.writeValueAsString(
                new RegisterRequest("existing", "password123")
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {

        ensureUserExists("alice");

        String body = objectMapper.writeValueAsString(
                new AuthRequest("alice", DEFAULT_PASSWORD)
        );

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("token"))
                .andExpect(jsonPath("$.username").value("alice"))
                .andReturn();

        // Validação adicional: token deve ser um JWT válido
        Cookie tokenCookie = result.getResponse().getCookie("token");
        String[] parts = tokenCookie.getValue().split("\\.");
        assertEquals(3, parts.length, "Token deve ser JWT válido");
    }

    @Test
    void shouldReturnUnauthorizedWhenPasswordIsInvalid() throws Exception {

        ensureUserExists("bob");

        String body = objectMapper.writeValueAsString(
                new AuthRequest("bob", "wrongpassword")
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedWhenUserDoesNotExist() throws Exception {

        String body = objectMapper.writeValueAsString(
                new AuthRequest("ghost", "password123")
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldLogoutAndClearCookie() throws Exception {

        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie",
                        org.hamcrest.Matchers.containsString("Max-Age=0")));
    }

    // ============================
    // JWT VALIDATION TESTS
    // ============================

    @Test
    void shouldValidateJwtTokenStructure() throws Exception {

        ensureUserExists("jwttest1");

        String body = objectMapper.writeValueAsString(
                new AuthRequest("jwttest1", DEFAULT_PASSWORD)
        );

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("token"))
                .andReturn();

        Cookie tokenCookie = result.getResponse().getCookie("token");
        assertNotNull(tokenCookie);
        assertNotNull(tokenCookie.getValue());

        String token = tokenCookie.getValue();

        // JWT deve ter 3 partes separadas por ponto: header.payload.signature
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT deve ter 3 partes (header.payload.signature)");
        assertTrue(parts[0].length() > 0, "Header não deve estar vazio");
        assertTrue(parts[1].length() > 0, "Payload não deve estar vazio");
        assertTrue(parts[2].length() > 0, "Signature não deve estar vazio");
    }

    @Test
    void shouldValidateJwtTokenSignatureAndClaims() throws Exception {

        ensureUserExists("jwttest2");

        String body = objectMapper.writeValueAsString(
                new AuthRequest("jwttest2", DEFAULT_PASSWORD)
        );

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andReturn();

        Cookie tokenCookie = result.getResponse().getCookie("token");
        String token = tokenCookie.getValue();

        // Decodificar e validar assinatura
        DecodedJWT decoded = jwtUtil.decode(token);

        assertNotNull(decoded, "Token deve ser decodificável");

        // Validar claims
        assertEquals("jwttest2", jwtUtil.extractUsername(decoded));
        List<String> roles = jwtUtil.extractRoles(decoded);
        assertNotNull(roles);
        assertEquals(1, roles.size());
        assertEquals("ROLE_USER", roles.get(0));
    }

    @Test
    void shouldValidateJwtTokenExpiration() throws Exception {

        ensureUserExists("jwttest3");

        String body = objectMapper.writeValueAsString(
                new AuthRequest("jwttest3", DEFAULT_PASSWORD)
        );

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andReturn();

        Cookie tokenCookie = result.getResponse().getCookie("token");
        String token = tokenCookie.getValue();

        DecodedJWT decoded = jwtUtil.decode(token);

        assertNotNull(decoded.getExpiresAt());

        // Token deve expirar no futuro
        Instant expiresAt = decoded.getExpiresAt().toInstant();
        Instant now = Instant.now();

        assertTrue(expiresAt.isAfter(now), "Token deve expirar no futuro");

        // Token deve expirar em menos de 2 horas (margem de segurança)
        assertTrue(expiresAt.isBefore(now.plusSeconds(2 * 3600)),
                "Token deve expirar em menos de 2 horas");
    }

    @Test
    void shouldValidateJwtTokenWithRoles() throws Exception {

        ensureAdminExists("jwtadmin");

        String body = objectMapper.writeValueAsString(
                new AuthRequest("jwtadmin", DEFAULT_PASSWORD)
        );

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andReturn();

        Cookie tokenCookie = result.getResponse().getCookie("token");
        String token = tokenCookie.getValue();

        DecodedJWT decoded = jwtUtil.decode(token);

        List<String> roles = jwtUtil.extractRoles(decoded);

        assertNotNull(roles);
        assertEquals(2, roles.size(), "Admin deve ter 2 roles");
        assertTrue(roles.contains("ROLE_ADMIN"));
        assertTrue(roles.contains("ROLE_USER"));
    }

    @Test
    void shouldRejectInvalidTokenInProtectedEndpoint() throws Exception {

        mockMvc.perform(get("/api/users/me")
                .cookie(new Cookie("token", "invalid.token.signature")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAuthenticateWithValidJwtToken() throws Exception {

        Cookie tokenCookie = loginAndReturnCookie("jwtuser");

        // Usar o token para acessar endpoint protegido
        mockMvc.perform(get("/api/users/me")
                .cookie(tokenCookie))
                .andExpect(status().isOk());
    }
}