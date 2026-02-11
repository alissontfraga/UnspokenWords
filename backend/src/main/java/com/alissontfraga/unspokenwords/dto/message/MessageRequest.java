package com.alissontfraga.unspokenwords.dto.message;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;

public record MessageRequest(

    @NotBlank
    String content,

    @NotBlank
    String category,

    @NotBlank
    String forPerson,

    LocalDate date
) {}
