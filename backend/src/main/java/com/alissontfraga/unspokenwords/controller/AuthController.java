package com.alissontfraga.unspokenwords.controller;

import java.util.Map;

import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.alissontfraga.unspokenwords.security.JwtUtil;
import com.alissontfraga.unspokenwords.dto.auth.AuthRequest;
import com.alissontfraga.unspokenwords.dto.auth.AuthResponse;
import com.alissontfraga.unspokenwords.dto.auth.RegisterRequest;
import com.alissontfraga.unspokenwords.dto.auth.RegisterResponse;
import com.alissontfraga.unspokenwords.entity.User;
import com.alissontfraga.unspokenwords.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "Sign up", description = "create a user")
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest dto ) {
        User user = userService.createUser(dto.username(), dto.password());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new RegisterResponse(user.getId(), user.getUsername()));
    }

    @Operation(summary = "Log in", description = "log in to a user account")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {

    User user = userService.findByUsername(req.username());

    if (user == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    if (!passwordEncoder.matches(req.password(), user.getPassword())) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    String token = jwtUtil.generateToken(user);
    return ResponseEntity.ok(new AuthResponse(token, user.getUsername()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Nada para invalidar no servidor
        /* Logout só no front-end, por ser um MVP, caso for mais profissional posso implementar o logout real + refresh tokens + Cookies HTTP Only + rotação de refresh tokens */
        return ResponseEntity.ok(
            Map.of("message", "Logged out successfully")
        );
    }

}
