package com.alissontfraga.unspokenwords.dto.message;

import java.time.LocalDate;

import com.alissontfraga.unspokenwords.entity.Message;

public record MessageResponse(
    Long id,
    String content,
    String category,
    String forPerson,
    LocalDate date
) {

    public MessageResponse(Message message) {
        this(
            message.getId(),
            message.getContent(),
            message.getCategory(),
            message.getForPerson(),
            message.getDate()
        );
    }
}
