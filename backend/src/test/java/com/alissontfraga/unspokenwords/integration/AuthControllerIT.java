package com.alissontfraga.unspokenwords.integration;

import com.alissontfraga.unspokenwords.dto.auth.AuthRequest;
import com.alissontfraga.unspokenwords.dto.auth.RegisterRequest;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("token"))
                .andExpect(jsonPath("$.username").value("alice"));
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
}