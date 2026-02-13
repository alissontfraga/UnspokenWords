package com.alissontfraga.unspokenwords.controller;

import java.time.Duration;

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
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "Sign up", description = "create a new user account")
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest dto ) {
        User user = userService.createUser(dto.username(), dto.password());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new RegisterResponse(user.getId(), user.getUsername()));
    }

    @Operation(summary = "Log in", description = "Logs in a user and sets an HttpOnly authentication cookie")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody AuthRequest req,
            HttpServletResponse response
    ) {

        User user = userService.findByUsername(req.username());

        if (user == null ||
            !passwordEncoder.matches(req.password(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwtUtil.generateToken(user);

        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(false) // true em produção (HTTPS)
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ofHours(1))
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok(new AuthResponse(user.getUsername()));
    }

   @Operation(summary = "Log out", description = "Logs out the current user by clearing the HttpOnly authentication cookie")
   @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {

        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(false) // true em produção
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ZERO)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok().build();
    }


}
