package com.creditbank.calculator.api;

import com.creditbank.calculator.dto.request.ScoringDataDto;
import com.creditbank.calculator.dto.response.CreditDto;
import com.creditbank.calculator.dto.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

public interface CreditApi {

    @Operation(
            summary = "Calculate credit conditions",
            description = "Performs calculateCredit and returns credit conditions with payment schedule."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Credit calculated"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error or malformed request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Validation error",
                                            value = """
                                        {
                                          "timestamp": "2026-03-12T10:15:30.123",
                                          "status": 400,
                                          "error": "Validation failed",
                                          "message": "Invalid request parameters",
                                          "errors": {
                                            "term": "Term must be at least 6 months"
                                            }
                                        }
                                        """
                                    ),
                                    @ExampleObject(
                                            name = "Deserialization error",
                                            value = """
                                        {
                                          "timestamp": "2026-03-12T10:15:30.123",
                                          "status": 400,
                                          "error": "Deserialization failed",
                                          "message": "Malformed JSON request",
                                          "errors": null
                                        }
                                        """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Scoring rejected (business rules failure)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Scoring rejection",
                                    value = """
                                {
                                  "timestamp": "2026-03-18T12:47:26.149",
                                  "status": 422,
                                  "error": "Scoring rejection",
                                  "message": "Rejection due to: work status - Unemployed",
                                  "errors": null
                                }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Internal error",
                                    value = """
                                {
                                  "timestamp": "2026-03-12T10:15:30.123",
                                  "status": 500,
                                  "error": "Internal server error",
                                  "message": "An unexpected error occurred",
                                  "errors": null
                                }
                                """
                            )
                    )
            )
    })
    ResponseEntity<CreditDto> calculateCredit(ScoringDataDto scoringDataDto);
}
