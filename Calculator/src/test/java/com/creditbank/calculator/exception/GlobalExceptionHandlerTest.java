package com.creditbank.calculator.exception;

import com.creditbank.calculator.enums.Gender;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @Test
    void shouldReturnBadRequestWithFieldErrorsForValidationException() {
        FieldError fe1 = new FieldError("request", "amount", "Amount is required");
        FieldError fe2 = new FieldError("request", "term", "Term is required");

        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fe1, fe2));

        ResponseEntity<ErrorResponse> response = handler.handleValidationExceptions(methodArgumentNotValidException);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Validation failed", response.getBody().getError());
        assertEquals("Invalid request parameters", response.getBody().getMessage());
        assertEquals(2, response.getBody().getErrors().size());
        assertEquals("Amount is required", response.getBody().getErrors().get("amount"));
        assertEquals("Term is required", response.getBody().getErrors().get("term"));
    }

    @Test
    void shouldReturnBadRequestForScoringException() {
        ScoringException ex = new ScoringException("bad scoring");

        ResponseEntity<ErrorResponse> response = handler.handleScoringException(ex);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Scoring failed", response.getBody().getError());
        assertEquals("bad scoring", response.getBody().getMessage());
    }

    @Test
    void shouldReturnInternalServerErrorForGenericException() {
        Exception ex = new RuntimeException("boom");

        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex);

        assertEquals(500, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Internal server error", response.getBody().getError());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
    }

    @Test
    void shouldReturnBadRequestForHttpMessageNotReadableWhenCauseIsInvalidFormatEnum() {
        InvalidFormatException ife = new InvalidFormatException(
                null, "INVALID", Gender.class);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException(
                "invalid", ife, null);

        ResponseEntity<ErrorResponse> response = handler.handleHttpMessageNotReadableException(ex);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Deserialization failed", response.getBody().getError());
        assertTrue(response.getBody().getMessage().contains("Gender"));
        assertTrue(response.getBody().getMessage().contains("INVALID"));
        assertTrue(response.getBody().getMessage().contains("MALE"));
    }

    @Test
    void shouldReturnBadRequestWithGenericMessageWhenHttpMessageNotReadableWithoutEnumCause() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException(
                "invalid", new RuntimeException("parse error"), null);

        ResponseEntity<ErrorResponse> response = handler.handleHttpMessageNotReadableException(ex);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Deserialization failed", response.getBody().getError());
        assertEquals("Invalid request format", response.getBody().getMessage());
    }
}

