package com.alissontfraga.unspokenwords.integration;

import com.alissontfraga.unspokenwords.dto.auth.RegisterRequest;
import com.alissontfraga.unspokenwords.entity.User;
import com.alissontfraga.unspokenwords.enums.Role;
import com.alissontfraga.unspokenwords.repository.UserRepository;
import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldCreateAdminWhenAuthenticatedAsAdmin() throws Exception {

        RegisterRequest request = new RegisterRequest("new_admin", "123");

        mockMvc.perform(post("/api/admin/create-admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.username").value("new_admin"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void shouldReturnForbiddenWhenNotAdmin() throws Exception {

        RegisterRequest request = new RegisterRequest("fail_admin", "123");

        mockMvc.perform(post("/api/admin/create-admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldDeleteUserWhenAdmin() throws Exception {

        User user = new User();
        user.setUsername("to_delete");
        user.setPassword(passwordEncoder.encode("123"));
        user.setRoles(Set.of(Role.ROLE_USER));
        userRepository.save(user);

        mockMvc.perform(delete("/api/admin/users/{username}", "to_delete"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void shouldReturnForbiddenWhenDeletingWithoutAdminRole() throws Exception {

        mockMvc.perform(delete("/api/admin/users/{username}", "any_user"))
                .andExpect(status().isForbidden());
    }
}
