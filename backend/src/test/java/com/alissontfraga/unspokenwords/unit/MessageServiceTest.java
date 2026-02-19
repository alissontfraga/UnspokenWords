package com.alissontfraga.unspokenwords.unit;

import com.alissontfraga.unspokenwords.dto.message.MessageRequest;
import com.alissontfraga.unspokenwords.dto.message.MessageResponse;
import com.alissontfraga.unspokenwords.dto.message.MessageUpdateRequest;
import com.alissontfraga.unspokenwords.entity.Message;
import com.alissontfraga.unspokenwords.entity.User;
import com.alissontfraga.unspokenwords.exception.BadRequestException;
import com.alissontfraga.unspokenwords.exception.ForbiddenException;
import com.alissontfraga.unspokenwords.exception.ResourceNotFoundException;
import com.alissontfraga.unspokenwords.repository.MessageRepository;
import com.alissontfraga.unspokenwords.service.MessageService;
import com.alissontfraga.unspokenwords.service.UserService;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @InjectMocks
    private MessageService messageService;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserService userService;

    private User mockUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        return user;
    }

    private Message mockMessage(Long id, User owner) {
        Message message = new Message();
        message.setId(id);
        message.setContent("content");
        message.setCategory("cat");
        message.setForPerson("someone");
        message.setDate(LocalDate.now());
        message.setOwner(owner);
        return message;
    }

    @Nested
    class ListMessages {

        @Test
        void shouldReturnListOfMessages() {
            User user = mockUser(1L, "alice");
            Message message = mockMessage(10L, user);

            when(userService.findByUsername("alice")).thenReturn(user);
            when(messageRepository.findByOwner_Id(1L))
                    .thenReturn(List.of(message));

            List<MessageResponse> result = messageService.list("alice");

            assertEquals(1, result.size());
            assertEquals("content", result.get(0).content());
        }
    }

    @Nested
    class CreateMessage {

        @Test
        void shouldCreateMessageWithProvidedDate() {
            User user = mockUser(1L, "alice");
            LocalDate date = LocalDate.now();

            MessageRequest dto = new MessageRequest(
                    "hello", "cat", "bob", date
            );

            when(userService.findByUsername("alice")).thenReturn(user);
            when(messageRepository.save(any(Message.class)))
                    .thenAnswer(invocation -> {
                        Message m = invocation.getArgument(0);
                        m.setId(99L);
                        return m;
                    });

            MessageResponse response =
                    messageService.create("alice", dto);

            assertEquals("hello", response.content());
            assertEquals(date, response.date());
        }

        @Test
        void shouldCreateMessageWithTodayWhenDateIsNull() {
            User user = mockUser(1L, "alice");

            MessageRequest dto = new MessageRequest(
                    "hello", "cat", "bob", null
            );

            when(userService.findByUsername("alice")).thenReturn(user);
            when(messageRepository.save(any(Message.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            MessageResponse response =
                    messageService.create("alice", dto);

            assertEquals(LocalDate.now(), response.date());
        }

        @Test
        void shouldThrowWhenDateIsInFuture() {
            User user = mockUser(1L, "alice");

            MessageRequest dto = new MessageRequest(
                    "hello", "cat", "bob",
                    LocalDate.now().plusDays(2)
            );

            when(userService.findByUsername("alice")).thenReturn(user);

            assertThrows(
                    BadRequestException.class,
                    () -> messageService.create("alice", dto)
            );

            verify(messageRepository, never()).save(any());
        }
    }

    @Nested
    class UpdateMessage {

        @Test
        void shouldUpdateFields() {
            User user = mockUser(1L, "alice");
            Message message = mockMessage(10L, user);

            MessageUpdateRequest dto =
                    new MessageUpdateRequest(
                            "new", "newCat", "newPerson",
                            LocalDate.now()
                    );

            when(messageRepository
                    .findByIdAndOwner_Username(10L, "alice"))
                    .thenReturn(Optional.of(message));

            MessageResponse response =
                    messageService.update(10L, "alice", dto);

            assertEquals("new", response.content());
            assertEquals("newCat", response.category());
            assertEquals("newPerson", response.forPerson());
        }

        @Test
        void shouldThrowWhenMessageNotFound() {
            MessageUpdateRequest dto =
                    new MessageUpdateRequest(null, null, null, null);

            when(messageRepository
                    .findByIdAndOwner_Username(1L, "alice"))
                    .thenReturn(Optional.empty());

            assertThrows(
                    ResourceNotFoundException.class,
                    () -> messageService.update(1L, "alice", dto)
            );
        }

        @Test
        void shouldThrowWhenUpdatingWithFutureDate() {
            User user = mockUser(1L, "alice");
            Message message = mockMessage(10L, user);

            when(messageRepository
                    .findByIdAndOwner_Username(10L, "alice"))
                    .thenReturn(Optional.of(message));

            MessageUpdateRequest dto =
                    new MessageUpdateRequest(
                            null, null, null,
                            LocalDate.now().plusDays(3)
                    );

            assertThrows(
                    BadRequestException.class,
                    () -> messageService.update(10L, "alice", dto)
            );
        }
    }

    @Nested
    class DeleteMessage {

        @Test
        void shouldDeleteWhenOwnerMatches() {
            User user = mockUser(1L, "alice");
            Message message = mockMessage(10L, user);

            when(userService.findByUsername("alice"))
                    .thenReturn(user);
            when(messageRepository.findById(10L))
                    .thenReturn(Optional.of(message));

            messageService.delete(10L, "alice");

            verify(messageRepository).delete(message);
        }

        @Test
        void shouldThrowWhenMessageNotFound() {
            User user = mockUser(1L, "alice");

            when(userService.findByUsername("alice"))
                    .thenReturn(user);
            when(messageRepository.findById(10L))
                    .thenReturn(Optional.empty());

            assertThrows(
                    ResourceNotFoundException.class,
                    () -> messageService.delete(10L, "alice")
            );
        }

        @Test
        void shouldThrowWhenUserIsNotOwner() {
            User owner = mockUser(1L, "alice");
            User another = mockUser(2L, "bob");
            Message message = mockMessage(10L, owner);

            when(userService.findByUsername("bob"))
                    .thenReturn(another);
            when(messageRepository.findById(10L))
                    .thenReturn(Optional.of(message));

            assertThrows(
                    ForbiddenException.class,
                    () -> messageService.delete(10L, "bob")
            );

            verify(messageRepository, never()).delete(any());
        }
    }
}

