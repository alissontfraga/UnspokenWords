package com.alissontfraga.unspokenwords.service;

import com.alissontfraga.unspokenwords.enums.Role;
import com.alissontfraga.unspokenwords.entity.User;
import com.alissontfraga.unspokenwords.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashSet;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private User createUserWithRole(String username, String rawPassword, Role... roles) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));

        user.setRoles(new HashSet<>(Arrays.asList(roles)));

        return userRepository.save(user);
    }

    public User createUser(String username, String rawPassword) {
        return createUserWithRole(username, rawPassword, Role.ROLE_USER);
    }

    public User createAdmin(String username, String rawPassword) {
        return createUserWithRole(username, rawPassword, Role.ROLE_ADMIN, Role.ROLE_USER);
    }


    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }



    @PreAuthorize("hasRole('ADMIN')")
    public void deleteByUsername(String username) {
        userRepository.findByUsername(username)
            .ifPresent(userRepository::delete);
    }
}
