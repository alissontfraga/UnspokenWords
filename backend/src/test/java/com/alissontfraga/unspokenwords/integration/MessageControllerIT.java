package com.alissontfraga.unspokenwords.integration;

import com.alissontfraga.unspokenwords.entity.Message;
import com.alissontfraga.unspokenwords.entity.User;
import com.alissontfraga.unspokenwords.enums.Role;
import com.alissontfraga.unspokenwords.repository.MessageRepository;
import com.alissontfraga.unspokenwords.repository.UserRepository;

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

import java.time.LocalDate;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MessageControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @BeforeEach
    void setup() {
        messageRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("user_test");
        user.setPassword(passwordEncoder.encode("123"));
        user.setRoles(Set.of(Role.ROLE_USER));

        userRepository.save(user);
    }

    @Test
    @WithMockUser(username = "user_test", roles = {"USER"})
    void shouldCreateMessage() throws Exception {

        String json = """
        {
          "content": "hello world",
          "category": "birthday",
          "forPerson": "Maria",
          "date": "2025-01-01"
        }
        """;

        mockMvc.perform(post("/api/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.content").value("hello world"))
                .andExpect(jsonPath("$.category").value("birthday"))
                .andExpect(jsonPath("$.forPerson").value("Maria"));
    }

    @Test
    @WithMockUser(username = "user_test", roles = {"USER"})
    void shouldListMessages() throws Exception {

        User user = userRepository.findByUsername("user_test").get();

        Message message = new Message();
        message.setContent("msg1");
        message.setCategory("birthday");
        message.setForPerson("Ana");
        message.setDate(LocalDate.now());
        message.setOwner(user);
        messageRepository.save(message);

        mockMvc.perform(get("/api/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].content").value("msg1"));
    }

    @Test
    @WithMockUser(username = "user_test", roles = {"USER"})
    void shouldUpdateMessage() throws Exception {

        User user = userRepository.findByUsername("user_test").get();

        Message message = new Message();
        message.setContent("old");
        message.setCategory("birthday");
        message.setForPerson("Ana");
        message.setDate(LocalDate.now());
        message.setOwner(user);
        message = messageRepository.save(message);

        String updateJson = """
        {
          "content": "updated",
          "category": "anniversary",
          "forPerson": "Carlos",
          "date": "2026-01-01"
        }
        """;

        mockMvc.perform(patch("/api/messages/{id}", message.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("updated"))
                .andExpect(jsonPath("$.category").value("anniversary"));
    }

    @Test
    @WithMockUser(username = "user_test", roles = {"USER"})
    void shouldDeleteMessage() throws Exception {

        User user = userRepository.findByUsername("user_test").get();

        Message message = new Message();
        message.setContent("to delete");
        message.setCategory("birthday");
        message.setForPerson("Ana");
        message.setDate(LocalDate.now());
        message.setOwner(user);
        message = messageRepository.save(message);

        mockMvc.perform(delete("/api/messages/{id}", message.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldReturnForbiddenWhenNotUserRole() throws Exception {

        mockMvc.perform(get("/api/messages"))
                .andExpect(status().isForbidden());
    }
}
