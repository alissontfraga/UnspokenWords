package com.alissontfraga.unspokenwords.integration;

import java.util.Set;

import jakarta.servlet.http.Cookie;
import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.alissontfraga.unspokenwords.enums.Role;
import com.alissontfraga.unspokenwords.entity.User;
import com.alissontfraga.unspokenwords.repository.UserRepository;
import com.alissontfraga.unspokenwords.security.JwtUtil;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class AbstractIntegrationTest {

    protected static final String DEFAULT_PASSWORD = "password123";

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected JwtUtil jwtUtil;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    // =========================
    // USER HELPERS
    // =========================

    protected User ensureUserExists(String username) {
        return ensureUserExists(username, Set.of(Role.ROLE_USER));
    }

    protected User ensureAdminExists(String username) {
        return ensureUserExists(
                username,
                Set.of(Role.ROLE_ADMIN, Role.ROLE_USER)
        );
    }

    protected User ensureUserExists(String username, Set<Role> roles) {

        return userRepository.findByUsername(username)
                .orElseGet(() -> {
                    User user = new User();
                    user.setUsername(username);
                    user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
                    user.setRoles(roles);
                    return userRepository.save(user);
                });
    }

    protected void deleteUser(String username) {
        userRepository.findByUsername(username)
                .ifPresent(userRepository::delete);
    }

    // AUTH HELPERS
    protected String generateToken(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow();
        return jwtUtil.generateToken(user);
    }

    protected Cookie loginAndReturnCookie(String username) throws Exception {

        ensureUserExists(username);

        String requestBody = objectMapper.writeValueAsString(
                new com.alissontfraga.unspokenwords.dto.auth.AuthRequest(
                        username,
                        DEFAULT_PASSWORD
                )
        );

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn();

        return result.getResponse().getCookie("token");
    }


    @Test
    void shouldLoadObjectMapperBean() {
        assertNotNull(objectMapper, "ObjectMapper não foi injetado");
    }
}