package com.alissontfraga.unspokenwords.unit;

import com.alissontfraga.unspokenwords.enums.Role;
import com.alissontfraga.unspokenwords.entity.User;
import com.alissontfraga.unspokenwords.exception.BadRequestException;
import com.alissontfraga.unspokenwords.repository.UserRepository;
import com.alissontfraga.unspokenwords.service.UserService;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Nested
    class CreateUser {

        @Test
        void shouldCreateUserSuccessfully() {
            String username = "alice";
            String rawPassword = "123";
            String encodedPassword = "encoded123";

            when(userRepository.existsByUsername(username)).thenReturn(false);
            when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
            when(userRepository.save(any(User.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            User result = userService.createUser(username, rawPassword);

            assertNotNull(result);
            assertEquals(username, result.getUsername());
            assertEquals(encodedPassword, result.getPassword());
            assertTrue(result.getRoles().contains(Role.ROLE_USER));
            assertEquals(1, result.getRoles().size());

            verify(passwordEncoder).encode(rawPassword);
            verify(userRepository).save(any(User.class));
        }

        @Test
        void shouldThrowExceptionWhenUsernameAlreadyExists() {
            when(userRepository.existsByUsername("alice")).thenReturn(true);

            assertThrows(
                    BadRequestException.class, 
                    () -> userService.createUser("alice", "123")
            );

            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    class CreateAdmin {

        @Test
        void shouldCreateAdminWithBothRoles() {
            String username = "admin";
            String rawPassword = "admin123";
            String encodedPassword = "encodedAdmin";

            when(userRepository.existsByUsername(username)).thenReturn(false);
            when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
            when(userRepository.save(any(User.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            User result = userService.createAdmin(username, rawPassword);

            assertNotNull(result);
            assertEquals(username, result.getUsername());
            assertEquals(encodedPassword, result.getPassword());
            assertTrue(result.getRoles().contains(Role.ROLE_ADMIN));
            assertTrue(result.getRoles().contains(Role.ROLE_USER));
            assertEquals(2, result.getRoles().size());

            verify(passwordEncoder).encode(rawPassword);
            verify(userRepository).save(any(User.class));
        }

        @Test
        void shouldThrowExceptionWhenAdminUsernameAlreadyExists() {
            when(userRepository.existsByUsername("admin")).thenReturn(true);

            assertThrows(
                    BadRequestException.class, 
                    () -> userService.createAdmin("admin", "123")
            );

            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    class FindByUsername {

        @Test
        void shouldReturnUserWhenExists() {
            User user = new User();
            user.setUsername("flower");

            when(userRepository.findByUsername("flower"))
                    .thenReturn(Optional.of(user));

            User result = userService.findByUsername("flower");

            assertNotNull(result);
            assertEquals("flower", result.getUsername());
        }

        @Test
        void shouldThrowWhenUserNotFound() {
            when(userRepository.findByUsername("ghost"))
                    .thenReturn(Optional.empty());

            assertThrows(
                    UsernameNotFoundException.class,
                    () -> userService.findByUsername("ghost")
            );
        }
    }

    @Nested
    class DeleteByUsername {

        @Test
        void shouldDeleteWhenUserExists() {
            User user = new User();
            user.setUsername("alice");

            when(userRepository.findByUsername("alice"))
                    .thenReturn(Optional.of(user));

            userService.deleteByUsername("alice");

            verify(userRepository).delete(user);
        }

        @Test
        void shouldDoNothingWhenUserDoesNotExist() {
            when(userRepository.findByUsername("ghost"))
                    .thenReturn(Optional.empty());

            userService.deleteByUsername("ghost");

            verify(userRepository, never()).delete(any(User.class));
        }
    }
}
