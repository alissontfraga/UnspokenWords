package com.alissontfraga.unspokenwords.unit;

import com.alissontfraga.unspokenwords.exception.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    @Test
    void shouldHandleResourceNotFound() {

        ResourceNotFoundException ex =
                new ResourceNotFoundException("Not found");

        ResponseEntity<ApiError> response =
                handler.handleNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Not found", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void shouldHandleForbidden() {

        ForbiddenException ex =
                new ForbiddenException("Forbidden");

        ResponseEntity<ApiError> response =
                handler.handleForbidden(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Forbidden", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void shouldHandleBadRequest() {

        BadRequestException ex =
                new BadRequestException("Bad request");

        ResponseEntity<ApiError> response =
                handler.handleBadRequest(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Bad request", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }
}
