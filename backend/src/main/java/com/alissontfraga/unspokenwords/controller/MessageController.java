package com.alissontfraga.unspokenwords.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.alissontfraga.unspokenwords.dto.message.MessageRequest;
import com.alissontfraga.unspokenwords.dto.message.MessageResponse;
import com.alissontfraga.unspokenwords.dto.message.MessageUpdateRequest;
import com.alissontfraga.unspokenwords.service.MessageService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    public ResponseEntity<List<MessageResponse>> list(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(
                messageService.list(userDetails.getUsername())
        );
    }

    @PostMapping
    public ResponseEntity<MessageResponse> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid MessageRequest dto
    ) {
        MessageResponse created =
                messageService.create(userDetails.getUsername(), dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MessageResponse> update(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid MessageUpdateRequest dto
    ) {
        return ResponseEntity.ok(
                messageService.update(
                        id,
                        userDetails.getUsername(),
                        dto
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        messageService.delete(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
