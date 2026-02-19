package com.alissontfraga.unspokenwords.unit;

import com.alissontfraga.unspokenwords.enums.Role;
import com.alissontfraga.unspokenwords.entity.User;
import com.alissontfraga.unspokenwords.repository.UserRepository;
import com.alissontfraga.unspokenwords.service.CustomUserDetailsService;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private UserRepository userRepository;

    private User mockUser(String username, String password, Set<Role> roles) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRoles(roles);
        return user;
    }

    @Nested
    class LoadUserByUsername {

        @Test
        void shouldReturnUserDetailsWhenUserExists() {
            User user = mockUser(
                    "alice",
                    "encodedPassword",
                    Set.of(Role.ROLE_USER, Role.ROLE_ADMIN)
            );

            when(userRepository.findByUsername("alice"))
                    .thenReturn(Optional.of(user));

            UserDetails userDetails =
                    customUserDetailsService.loadUserByUsername("alice");

            assertNotNull(userDetails);
            assertEquals("alice", userDetails.getUsername());
            assertEquals("encodedPassword", userDetails.getPassword());

            assertEquals(2, userDetails.getAuthorities().size());
            assertTrue(
                    userDetails.getAuthorities()
                            .stream()
                            .map(GrantedAuthority::getAuthority)
                            .toList()
                            .contains("ROLE_USER")
            );
            assertTrue(
                    userDetails.getAuthorities()
                            .stream()
                            .map(GrantedAuthority::getAuthority)
                            .toList()
                            .contains("ROLE_ADMIN")
            );
        }

        @Test
        void shouldThrowWhenUserNotFound() {
            when(userRepository.findByUsername("ghost"))
                    .thenReturn(Optional.empty());

            UsernameNotFoundException exception = assertThrows(
                    UsernameNotFoundException.class,
                    () -> customUserDetailsService
                            .loadUserByUsername("ghost")
            );

            assertEquals(
                    "User not found: ghost",
                    exception.getMessage()
            );
        }
    }
}
