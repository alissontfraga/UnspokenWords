package com.alissontfraga.unspokenwords.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.alissontfraga.unspokenwords.dto.message.MessageRequest;
import com.alissontfraga.unspokenwords.dto.message.MessageResponse;
import com.alissontfraga.unspokenwords.dto.message.MessageUpdateRequest;
import com.alissontfraga.unspokenwords.service.MessageService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@PreAuthorize("hasRole('USER')")
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @Operation(summary = "List messages", description = "Lists all messages belonging to the authenticated user")
    @GetMapping
    public ResponseEntity<List<MessageResponse>> list(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                messageService.list(authentication.getName())
        );
    }

    @Operation(summary = "Create message", description = "Creates a new message for the authenticated user")
    @PostMapping
    public ResponseEntity<MessageResponse> create(
            Authentication authentication,
            @RequestBody @Valid MessageRequest dto
    ) {
        MessageResponse created =
                messageService.create(authentication.getName(), dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @Operation(summary = "Update message", description = "Updates a message belonging to the authenticated user")
    @PatchMapping("/{id}")
    public ResponseEntity<MessageResponse> update(
            @PathVariable Long id,
            Authentication authentication,
            @RequestBody @Valid MessageUpdateRequest dto
    ) {
        return ResponseEntity.ok(
                messageService.update(
                        id,
                        authentication.getName(),
                        dto
                )
        );
    }

    @Operation(summary = "Delete Message", description = "Deletes the message with the given ID if it belongs to the authenticated user")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            Authentication authentication
    ) {
        messageService.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
