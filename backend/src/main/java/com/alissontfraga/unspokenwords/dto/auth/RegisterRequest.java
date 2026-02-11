package com.alissontfraga.unspokenwords.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank(message = "Username cannot be blank") String username,
    @NotBlank @Size(min = 3) String password
) {}
