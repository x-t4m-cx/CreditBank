package com.creditbank.gateway.handler;

import com.creditbank.gateway.dto.response.ErrorResponse;
import com.creditbank.gateway.exception.BadRequestException;
import com.creditbank.gateway.exception.DeniedException;
import com.creditbank.gateway.exception.StatementNotFoundException;
import com.creditbank.gateway.exception.VerifyException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(DeniedException.class)
    public ResponseEntity<ErrorResponse> handleDeniedException(DeniedException ex) {

        log.warn("Denied Exception", ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .error("Denied")
                .message(ex.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(errorResponse);
    }
    @ExceptionHandler(VerifyException.class)
    public ResponseEntity<ErrorResponse> handleVerifyException(
            VerifyException ex){
        log.warn("Code not verify", ex);
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .error("Code not verify")
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        log.warn("Validation error", ex);

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String message = error.getDefaultMessage();

            if (error instanceof FieldError fieldError) {
                errors.put(fieldError.getField(), message);
            } else {
                errors.put(error.getObjectName(), message);
            }
        });

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation failed")
                .message("Invalid request parameters")
                .errors(errors)
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(StatementNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStatementNotFoundException(StatementNotFoundException ex) {
        log.error("Statement not found error", ex);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Statement not found error")
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex) {

        log.warn("Deserialization error", ex);

        String message = switch (getRootCause(ex)) {
            case MismatchedInputException mie -> buildMismatchedInputMessage(mie);
            case JsonParseException jpe -> "Malformed JSON request";
            case null, default -> "Invalid request format";
        };

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Deserialization failed")
                .message(message)
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex) {

        log.error("bad request exception", ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("bad request")
                .message(ex.getMessage())
                .errors(ex.getErrors())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {

        log.error("Unhandled exception", ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal server error")
                .message("An unexpected error occurred")
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }

    private Throwable getRootCause(Throwable ex) {
        Throwable result = ex;
        while (result.getCause() != null && result.getCause() != result) {
            result = result.getCause();
        }
        return result;
    }

    private String buildMismatchedInputMessage(MismatchedInputException ex) {
        String field = getFieldPath(ex);

        if ("unknown".equals(field)) {
            return "Request body is missing or malformed";
        }

        return String.format(
                "Missing or invalid value for field '%s'",
                field
        );
    }

    private String getFieldPath(JsonMappingException ex) {
        if (ex.getPath() == null || ex.getPath().isEmpty()) {
            return "unknown";
        }

        JsonMappingException.Reference lastRef = ex.getPath().getLast();

        if (lastRef.getFieldName() != null) {
            return lastRef.getFieldName();
        } else if (lastRef.getIndex() >= 0) {
            return "[" + lastRef.getIndex() + "]";
        }

        return "unknown";
    }
}
