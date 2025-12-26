package com.alissontfraga.unspokenwords.dto.message;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;

public record MessageRequestDTO(
    @NotBlank(message = "Content is required") String content,
    
    @NotBlank(message = "Category is required") String category,

    @NotBlank(message = "Recipient is required") String forPerson,

    LocalDate date
) {}
