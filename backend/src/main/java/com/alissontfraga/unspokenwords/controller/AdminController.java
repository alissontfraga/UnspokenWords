package com.alissontfraga.unspokenwords.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alissontfraga.unspokenwords.dto.auth.RegisterRequest;
import com.alissontfraga.unspokenwords.dto.auth.RegisterResponse;
import com.alissontfraga.unspokenwords.entity.User;
import com.alissontfraga.unspokenwords.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @Operation(summary = "Create admin user", description = "add an admin user")
    @PostMapping("/create-admin")
    public ResponseEntity<RegisterResponse> createAdmin(
            @Valid @RequestBody RegisterRequest dto
    ) {
        User user = userService.createAdmin(dto.username(), dto.password());

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new RegisterResponse(user.getId(), user.getUsername()));
    }


    @Operation(summary = "Delete user", description = "Delete a user")
    @DeleteMapping("/users/{username}")
    public ResponseEntity<Void> delete(@PathVariable String username) {
        userService.deleteByUsername(username);
        return ResponseEntity.noContent().build();
    }
}

