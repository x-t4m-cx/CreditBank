package com.creditbank.gateway.api;

import com.creditbank.gateway.dto.request.FinishRegistrationRequestDto;
import com.creditbank.gateway.dto.request.LoanOfferDto;
import com.creditbank.gateway.dto.request.LoanStatementRequestDto;
import com.creditbank.gateway.dto.response.ErrorResponse;
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
            summary = "Create loan statement",
            description = """
                    Accepts a client loan statement and forwards it to the Statement microservice
                    to generate loan offers.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Loan offers created"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation or deserialization error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
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
                    description = "Internal server or downstream error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    ResponseEntity<List<LoanOfferDto>> createLoanStatement(LoanStatementRequestDto loanStatementRequestDto);

    @Operation(
            summary = "Select loan offer",
            description = """
                    Forwards the selected loan offer to the Deal microservice and updates
                    the target statement.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Offer selected"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation or deserialization error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Statement not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server or downstream error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<Void> selectOffer(LoanOfferDto loanOfferDto);

    @Operation(
            summary = "Finish statement registration",
            description = """
                    Forwards the completed registration and scoring data to the Deal microservice
                    for final credit calculation.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registration completed"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation or deserialization error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Statement not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Statement denied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2026-03-28T13:33:47.1076646",
                                              "status": 422,
                                              "error": "Denied",
                                              "message": "Rejection due to: work status - Unemployed",
                                              "errors": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server or downstream error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<Void> finishRegistration(FinishRegistrationRequestDto request, String statementId);
}
