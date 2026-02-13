package com.alissontfraga.unspokenwords.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alissontfraga.unspokenwords.dto.auth.UserResponse;
import com.alissontfraga.unspokenwords.entity.User;
import com.alissontfraga.unspokenwords.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get current user", description = "Retrieves the currently authenticated user's information")
    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        return new UserResponse(user.getId(), user.getUsername());
    }
}
