package com.creditbank.deal.handler;

import com.creditbank.deal.dto.response.ErrorResponse;
import com.creditbank.deal.exception.DeniedException;
import com.creditbank.deal.exception.VerifyException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


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

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(
            EntityNotFoundException ex
    ) {
        log.warn("Not found error", ex);
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Entity not found")
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex) {

        log.warn("Deserialization error", ex);

        String message = switch (getRootCause(ex)) {
            case InvalidFormatException ife -> buildInvalidFormatMessage(ife);
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

    private Throwable getRootCause(Throwable ex) {
        Throwable result = ex;
        while (result.getCause() != null && result.getCause() != result) {
            result = result.getCause();
        }
        return result;
    }

    private String buildInvalidFormatMessage(InvalidFormatException ex) {

        String field = getFieldPath(ex);
        Object value = ex.getValue();
        Class<?> targetType = ex.getTargetType();

        if (targetType != null && targetType.isEnum()) {
            return String.format(
                    "Invalid value '%s' for enum %s. Allowed values: %s",
                    value,
                    targetType.getSimpleName(),
                    Arrays.toString(targetType.getEnumConstants())
            );
        }

        return String.format(
                "Invalid value '%s' for field '%s'. Expected type: %s",
                value,
                field,
                targetType != null ? targetType.getSimpleName() : "unknown"
        );
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
}