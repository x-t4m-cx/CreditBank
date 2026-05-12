package com.creditbank.statement.api;

import com.creditbank.statement.dto.request.LoanOfferDto;
import com.creditbank.statement.dto.request.LoanStatementRequestDto;
import com.creditbank.statement.dto.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface StatementApi {
    @Operation(
            summary = "Create statement, client and generate 4 loan offers",
            description = """
                    Calls the MC deal to create and save a statement
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Loan offers created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoanOfferDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation/deserialization error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "ErrorResponse",
                                    value = """
                                            {
                                              "timestamp": "2026-03-12T10:15:30.123",
                                              "status": 400,
                                              "error": "Validation failed",
                                              "message": "Invalid request parameters",
                                              "errors": {
                                                "amount": "Amount must be at least 20000"
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "ErrorResponse",
                                    value = """
                                            {
                                              "timestamp": "2026-03-12T10:15:30.123",
                                              "status": 500,
                                              "error": "Internal server error",
                                              "message": "An unexpected error occurred"
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<List<LoanOfferDto>> createStatement(LoanStatementRequestDto request);

    @Operation(
            summary = "Select loan offer",
            description = """
                    Updates the statement with the selected offer:\s
                     sends a request to MC deal to update the database
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Offer selected"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation/deserialization error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "ErrorResponse",
                                    value = """
                                            {
                                              "timestamp": "2026-03-12T10:15:30.123",
                                              "status": 400,
                                              "error": "Deserialization failed",
                                              "message": "Malformed JSON request"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Statement not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "ErrorResponse",
                                    value = """
                                            {
                                              "timestamp": "2026-03-12T10:15:30.123",
                                              "status": 404,
                                              "error": "Statement not found error",
                                              "message": "Statement not found - id: 3fa85f64-5717-4562-b3fc-2c963f66afa6"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "ErrorResponse",
                                    value = """
                                            {
                                              "timestamp": "2026-03-12T10:15:30.123",
                                              "status": 500,
                                              "error": "Internal server error",
                                              "message": "An unexpected error occurred"
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<Void> applyOffer(LoanOfferDto offerDto);
}
