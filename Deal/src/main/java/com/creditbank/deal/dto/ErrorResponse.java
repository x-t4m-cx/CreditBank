package com.creditbank.deal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Builder
@Schema(name = "ErrorResponse", description = "Standard error response body")
public class ErrorResponse {
    @Schema(description = "Error timestamp", example = "2026-03-12T10:15:30.123")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "Short error type", example = "Validation failed")
    private String error;

    @Schema(description = "Human-readable error message", example = "Invalid request parameters")
    private String message;

    @Schema(description = "Field validation errors (field -> message)")
    private Map<String, String> errors;
}