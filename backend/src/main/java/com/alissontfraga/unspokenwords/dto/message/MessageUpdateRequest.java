package com.alissontfraga.unspokenwords.dto.message;

import java.time.LocalDate;

public record MessageUpdateRequest(

    String content,
    String category,
    String forPerson,
    LocalDate date
) {}
