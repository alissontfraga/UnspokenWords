package com.alissontfraga.unspokenwords.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alissontfraga.unspokenwords.entity.User;
import com.alissontfraga.unspokenwords.exception.BadRequestException;
import com.alissontfraga.unspokenwords.exception.ForbiddenException;
import com.alissontfraga.unspokenwords.exception.ResourceNotFoundException;
import com.alissontfraga.unspokenwords.dto.message.MessageRequest;
import com.alissontfraga.unspokenwords.dto.message.MessageResponse;
import com.alissontfraga.unspokenwords.dto.message.MessageUpdateRequest;
import com.alissontfraga.unspokenwords.entity.Message;
import com.alissontfraga.unspokenwords.repository.MessageRepository;

import lombok.RequiredArgsConstructor;
@Transactional
@RequiredArgsConstructor
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserService userService;

    /* =======================
       List
       ======================= */

    public List<MessageResponse> list(String username) {
        User user = userService.findByUsername(username);

        return messageRepository.findByOwner_Id(user.getId())
                .stream()
                .map(MessageResponse::new)
                .toList();
    }

    /* =======================
       Create
       ======================= */

    public MessageResponse create(String username, MessageRequest dto) {

        User user = userService.findByUsername(username);

        Message message = new Message();
        message.setContent(dto.content());
        message.setCategory(dto.category());
        message.setForPerson(dto.forPerson());

        if (dto.date() != null) {
            validateDate(dto.date());
            message.setDate(dto.date());
        } else {
            message.setDate(LocalDate.now());
        }

        message.setOwner(user);

        return new MessageResponse(
                messageRepository.save(message)
        );
    }

    /* =======================
       Update (partial)
       ======================= */

    public MessageResponse update(
            Long id,
            String username,
            MessageUpdateRequest dto
    ) {
        User user = userService.findByUsername(username);

        Message message = messageRepository.findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException("Message not found")
            );

        if (!message.getOwner().getId().equals(user.getId())) {
            throw new ForbiddenException("You can't update this message");
        }

        if (dto.content() != null) message.setContent(dto.content());
        if (dto.category() != null) message.setCategory(dto.category());
        if (dto.forPerson() != null) message.setForPerson(dto.forPerson());

        if (dto.date() != null) {
            validateDate(dto.date());
            message.setDate(dto.date());
        }

        return new MessageResponse(
                messageRepository.save(message)
        );
    }

    /* =======================
       Delete
       ======================= */

    public void delete(Long id, String username) {

        User user = userService.findByUsername(username);

        Message message = messageRepository.findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException("Message not found")
            );

        if (!message.getOwner().getId().equals(user.getId())) {
            throw new ForbiddenException("You can't delete this message");
        }

        messageRepository.delete(message);
    }

    /* =======================
       Secondaries
       ======================= */

    private void validateDate(LocalDate date) {
        if (date.isAfter(LocalDate.now().plusDays(1))) {
            throw new BadRequestException(
                "Message date cannot be in the future"
            );
        }
    }
}
