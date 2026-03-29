package com.creditbank.deal.handler;

import com.creditbank.deal.dto.ErrorResponse;
import com.creditbank.deal.enums.Gender;
import com.creditbank.deal.exception.DeniedException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @Test
    void shouldReturnNotFoundForEntityNotFoundException() {
        EntityNotFoundException ex = new EntityNotFoundException("Client not found with id: 123");

        ResponseEntity<ErrorResponse> response = handler.handleEntityNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Entity not found", response.getBody().getError());
        assertEquals("Client not found with id: 123", response.getBody().getMessage());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getStatus());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void shouldReturnBadRequestWithFieldErrorsForValidationException() {
        FieldError fe1 = new FieldError("request", "amount", "Amount is required");
        FieldError fe2 = new FieldError("request", "term", "Term is required");

        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fe1, fe2));

        ResponseEntity<ErrorResponse> response =
                handler.handleValidationExceptions(methodArgumentNotValidException);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Validation failed", response.getBody().getError());
        assertEquals("Invalid request parameters", response.getBody().getMessage());
        assertEquals(2, response.getBody().getErrors().size());
        assertEquals("Amount is required", response.getBody().getErrors().get("amount"));
        assertEquals("Term is required", response.getBody().getErrors().get("term"));
    }

    @Test
    void shouldHandleObjectErrorWithoutClassCastException() {
        ObjectError error = new ObjectError("request", "global error");

        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(error));

        ResponseEntity<ErrorResponse> response =
                handler.handleValidationExceptions(methodArgumentNotValidException);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals("global error", response.getBody().getErrors().get("request"));
    }

    @Test
    void shouldHandleValidationExceptionWithNoErrors() {
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Collections.emptyList());

        ResponseEntity<ErrorResponse> response =
                handler.handleValidationExceptions(methodArgumentNotValidException);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getErrors().size());
    }

    @Test
    void shouldReturnUnprocessableEntityForDeniedException() {
        DeniedException ex = new DeniedException("Credit calculation failed due to low score");

        ResponseEntity<ErrorResponse> response = handler.handleDeniedException(ex);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Denied", response.getBody().getError());
        assertEquals("Credit calculation failed due to low score", response.getBody().getMessage());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getBody().getStatus());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void shouldReturnUnprocessableEntityWithCorrectErrorStructureForDeniedException() {
        DeniedException ex = new DeniedException("Application denied");

        ResponseEntity<ErrorResponse> response = handler.handleDeniedException(ex);

        assertNotNull(response.getBody());
        assertEquals("Denied", response.getBody().getError());
        assertEquals("Application denied", response.getBody().getMessage());
        assertNull(response.getBody().getErrors());
    }

    @Test
    void shouldReturnBadRequestForInvalidEnumValue() {
        InvalidFormatException ife = new InvalidFormatException(
                null, "INVALID", Gender.class);

        HttpMessageNotReadableException ex =
                new HttpMessageNotReadableException("invalid", ife, null);

        ResponseEntity<ErrorResponse> response =
                handler.handleHttpMessageNotReadableException(ex);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Deserialization failed", response.getBody().getError());
        String msg = response.getBody().getMessage();
        assertTrue(msg.contains("Gender"));
        assertTrue(msg.contains("INVALID"));
        assertTrue(msg.contains("MALE") || msg.contains("FEMALE"));
    }

    @Test
    void shouldReturnBadRequestForInvalidFormatNonEnum() {
        InvalidFormatException ife = new InvalidFormatException(
                null, "abc", Integer.class);

        HttpMessageNotReadableException ex =
                new HttpMessageNotReadableException("invalid", ife, null);

        ResponseEntity<ErrorResponse> response =
                handler.handleHttpMessageNotReadableException(ex);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        String msg = response.getBody().getMessage();
        assertTrue(msg.contains("abc"));
        assertTrue(msg.contains("Integer"));
    }

    @Test
    void shouldReturnBadRequestForMismatchedInputExceptionKnownField() {
        MismatchedInputException mie = mock(MismatchedInputException.class);
        JsonMappingException.Reference ref = mock(JsonMappingException.Reference.class);
        when(ref.getFieldName()).thenReturn("amount");
        when(mie.getPath()).thenReturn(List.of(ref));

        HttpMessageNotReadableException ex =
                new HttpMessageNotReadableException("invalid", mie, null);

        ResponseEntity<ErrorResponse> response =
                handler.handleHttpMessageNotReadableException(ex);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        assertTrue(response.getBody().getMessage().contains("amount"));
    }

    @Test
    void shouldReturnMalformedMessageForMismatchedUnknownField() {
        MismatchedInputException mie = mock(MismatchedInputException.class);
        when(mie.getPath()).thenReturn(Collections.emptyList());

        HttpMessageNotReadableException ex =
                new HttpMessageNotReadableException("invalid", mie, null);

        ResponseEntity<ErrorResponse> response =
                handler.handleHttpMessageNotReadableException(ex);

        assertEquals("Request body is missing or malformed", response.getBody().getMessage());
    }

    @Test
    void shouldReturnMalformedJsonMessage() {
        JsonParseException jpe = new JsonParseException(null, "bad json");

        HttpMessageNotReadableException ex =
                new HttpMessageNotReadableException("invalid", jpe, null);

        ResponseEntity<ErrorResponse> response =
                handler.handleHttpMessageNotReadableException(ex);

        assertEquals("Malformed JSON request", response.getBody().getMessage());
    }

    @Test
    void shouldReturnGenericMessageWhenUnknownCause() {
        HttpMessageNotReadableException ex =
                new HttpMessageNotReadableException(
                        "invalid", new RuntimeException("parse error"), null);

        ResponseEntity<ErrorResponse> response =
                handler.handleHttpMessageNotReadableException(ex);

        assertEquals("Invalid request format", response.getBody().getMessage());
    }

    @Test
    void shouldReturnInternalServerErrorForGenericException() {
        Exception ex = new RuntimeException("Unexpected error occurred");

        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Internal server error", response.getBody().getError());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getBody().getStatus());
        assertNotNull(response.getBody().getTimestamp());
        assertNull(response.getBody().getErrors());
    }

    @Test
    void shouldReturnInternalServerErrorForNullPointerException() {
        NullPointerException ex = new NullPointerException("Something is null");

        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCode().value());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
    }

    @Test
    void shouldReturnInternalServerErrorForIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");

        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCode().value());
        assertEquals("Internal server error", response.getBody().getError());
    }

    @Test
    void shouldReturnUnknownWhenPathIsNull() {
        JsonMappingException ex = mock(JsonMappingException.class);
        when(ex.getPath()).thenReturn(null);

        String result = invokeGetFieldPath(ex);

        assertEquals("unknown", result);
    }

    @Test
    void shouldReturnUnknownWhenPathIsEmpty() {
        JsonMappingException ex = mock(JsonMappingException.class);
        when(ex.getPath()).thenReturn(Collections.emptyList());

        String result = invokeGetFieldPath(ex);

        assertEquals("unknown", result);
    }

    @Test
    void shouldReturnFieldNameForSimpleField() {
        JsonMappingException ex = mock(JsonMappingException.class);
        JsonMappingException.Reference ref = new JsonMappingException.Reference(new Object(), "term");
        when(ex.getPath()).thenReturn(List.of(ref));

        String result = invokeGetFieldPath(ex);

        assertEquals("term", result);
    }

    @Test
    void shouldReturnIndexForArrayElement() {
        JsonMappingException ex = mock(JsonMappingException.class);
        JsonMappingException.Reference ref = new JsonMappingException.Reference(new Object(), 2);
        when(ex.getPath()).thenReturn(List.of(ref));

        String result = invokeGetFieldPath(ex);

        assertEquals("[2]", result);
    }

    @Test
    void shouldReturnLastFieldNameForNestedPath() {
        JsonMappingException ex = mock(JsonMappingException.class);
        JsonMappingException.Reference ref1 = new JsonMappingException.Reference(new Object(), "user");
        JsonMappingException.Reference ref2 = new JsonMappingException.Reference(new Object(), "address");
        JsonMappingException.Reference ref3 = new JsonMappingException.Reference(new Object(), "city");
        when(ex.getPath()).thenReturn(List.of(ref1, ref2, ref3));

        String result = invokeGetFieldPath(ex);

        assertEquals("city", result);
    }

    @Test
    void shouldReturnLastElementWithIndexForMixedPath() {
        JsonMappingException ex = mock(JsonMappingException.class);
        JsonMappingException.Reference ref1 = new JsonMappingException.Reference(new Object(), "users");
        JsonMappingException.Reference ref2 = new JsonMappingException.Reference(new Object(), 0);
        JsonMappingException.Reference ref3 = new JsonMappingException.Reference(new Object(), "name");
        when(ex.getPath()).thenReturn(List.of(ref1, ref2, ref3));

        String result = invokeGetFieldPath(ex);

        assertEquals("name", result);
    }

    @Test
    void shouldReturnIndexWhenLastElementIsIndex() {
        JsonMappingException ex = mock(JsonMappingException.class);
        JsonMappingException.Reference ref1 = new JsonMappingException.Reference(new Object(), "matrix");
        JsonMappingException.Reference ref2 = new JsonMappingException.Reference(new Object(), 0);
        JsonMappingException.Reference ref3 = new JsonMappingException.Reference(new Object(), 1);
        when(ex.getPath()).thenReturn(List.of(ref1, ref2, ref3));

        String result = invokeGetFieldPath(ex);

        assertEquals("[1]", result);
    }

    @Test
    void shouldReturnRootCauseForNestedExceptions() {
        RuntimeException root = new RuntimeException("root");
        IllegalArgumentException middle = new IllegalArgumentException("middle", root);
        HttpMessageNotReadableException top = new HttpMessageNotReadableException("top", middle, null);

        Throwable result = invokeGetRootCause(top);

        assertEquals(root, result);
    }

    @Test
    void shouldReturnSelfWhenNoCause() {
        RuntimeException ex = new RuntimeException("no cause");

        Throwable result = invokeGetRootCause(ex);

        assertEquals(ex, result);
    }

    @Test
    void shouldBuildInvalidFormatMessageWithNullTargetType() {
        InvalidFormatException ife = new InvalidFormatException(null, "bad", null);

        String result = invokeBuildInvalidFormatMessage(ife);

        assertEquals("Invalid value 'bad' for field 'unknown'. Expected type: unknown", result);
    }

    @Test
    void shouldBuildInvalidFormatMessageWithField() {
        InvalidFormatException ife = mock(InvalidFormatException.class);
        when(ife.getValue()).thenReturn("abc");
        when(ife.getTargetType()).thenAnswer(invocation -> Integer.class);

        JsonMappingException.Reference ref = new JsonMappingException.Reference(new Object(), "term");
        when(ife.getPath()).thenReturn(List.of(ref));

        String result = invokeBuildInvalidFormatMessage(ife);

        assertEquals("Invalid value 'abc' for field 'term'. Expected type: Integer", result);
    }

    @Test
    void shouldBuildInvalidFormatMessageForEnumWithField() {
        InvalidFormatException ife = mock(InvalidFormatException.class);
        when(ife.getValue()).thenReturn("INVALID_ENUM");
        when(ife.getTargetType()).thenAnswer(invocation -> Gender.class);

        JsonMappingException.Reference ref = new JsonMappingException.Reference(new Object(), "gender");
        when(ife.getPath()).thenReturn(List.of(ref));

        String result = invokeBuildInvalidFormatMessage(ife);

        assertTrue(result.contains("Gender"));
        assertTrue(result.contains("INVALID_ENUM"));
        assertTrue(result.contains("MALE") || result.contains("FEMALE"));
    }

    @Test
    void shouldBuildMismatchedInputMessageWithKnownField() {
        MismatchedInputException mie = mock(MismatchedInputException.class);
        JsonMappingException.Reference ref = new JsonMappingException.Reference(new Object(), "amount");
        when(mie.getPath()).thenReturn(List.of(ref));

        String result = invokeBuildMismatchedInputMessage(mie);

        assertEquals("Missing or invalid value for field 'amount'", result);
    }

    @Test
    void shouldBuildMismatchedInputMessageWithUnknownField() {
        MismatchedInputException mie = mock(MismatchedInputException.class);
        when(mie.getPath()).thenReturn(Collections.emptyList());

        String result = invokeBuildMismatchedInputMessage(mie);

        assertEquals("Request body is missing or malformed", result);
    }

    @Test
    void shouldBuildMismatchedInputMessageWithNullPath() {
        MismatchedInputException mie = mock(MismatchedInputException.class);
        when(mie.getPath()).thenReturn(null);

        String result = invokeBuildMismatchedInputMessage(mie);

        assertEquals("Request body is missing or malformed", result);
    }

    private String invokeGetFieldPath(JsonMappingException ex) {
        try {
            java.lang.reflect.Method method = GlobalExceptionHandler.class
                    .getDeclaredMethod("getFieldPath", JsonMappingException.class);
            method.setAccessible(true);
            return (String) method.invoke(handler, ex);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke getFieldPath", e);
        }
    }

    private Throwable invokeGetRootCause(Throwable ex) {
        try {
            java.lang.reflect.Method method = GlobalExceptionHandler.class
                    .getDeclaredMethod("getRootCause", Throwable.class);
            method.setAccessible(true);
            return (Throwable) method.invoke(handler, ex);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke getRootCause", e);
        }
    }

    private String invokeBuildInvalidFormatMessage(InvalidFormatException ex) {
        try {
            java.lang.reflect.Method method = GlobalExceptionHandler.class
                    .getDeclaredMethod("buildInvalidFormatMessage", InvalidFormatException.class);
            method.setAccessible(true);
            return (String) method.invoke(handler, ex);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke buildInvalidFormatMessage", e);
        }
    }

    private String invokeBuildMismatchedInputMessage(MismatchedInputException ex) {
        try {
            java.lang.reflect.Method method = GlobalExceptionHandler.class
                    .getDeclaredMethod("buildMismatchedInputMessage", MismatchedInputException.class);
            method.setAccessible(true);
            return (String) method.invoke(handler, ex);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke buildMismatchedInputMessage", e);
        }
    }
}