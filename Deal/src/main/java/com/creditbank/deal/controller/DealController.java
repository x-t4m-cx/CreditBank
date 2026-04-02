package com.creditbank.deal.controller;

import com.creditbank.deal.dto.response.ErrorResponse;
import com.creditbank.deal.dto.request.FinishRegistrationRequestDto;
import com.creditbank.deal.dto.response.LoanOfferDto;
import com.creditbank.deal.dto.request.LoanStatementRequestDto;
import com.creditbank.deal.service.DealService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/deal")
public class DealController {
    private final DealService service;

    @PostMapping("/statement")
    @Operation(
            summary = "Create statement, client and generate 4 loan offers",
            description = """
                    Creates and persists Client and Statement entities, then calls Calculator
                    microservice to generate 4 loan offers. Each offer is enriched with the
                    statement ID and returned sorted from worst to best conditions.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created entities and generated offers"),
            @ApiResponse(responseCode = "400", description = "Validation error or malformed request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
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
                                                      "message": "Invalid value 'abc' for field 'amount'. Expected type: BigDecimal",
                                                      "errors": null
                                                    }
                                                    """
                                    )
                            }
                    )),
            @ApiResponse(responseCode = "500", description = "Unexpected error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
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
                            )))
    })
    public ResponseEntity<List<LoanOfferDto>> createStatement(
            @Valid @RequestBody LoanStatementRequestDto request) {
        return ResponseEntity.ok(service.createStatement(request));
    }


    @PostMapping("/offer/select")
    @Operation(
            summary = "Select loan offer",
            description = """
                    Updates the statement with the selected offer: 
                    changes status, adds to history, and saves the offer.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated statement with offer"),
            @ApiResponse(responseCode = "400", description = "Validation error or malformed request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
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
                                                      "message": "Invalid value 'abc' for field 'amount'. Expected type: BigDecimal",
                                                      "errors": null
                                                    }
                                                    """
                                    )
                            }
                    )),
            @ApiResponse(responseCode = "404", description = "Not found entity",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Entity not found",
                                    value = """
                                            {
                                               "timestamp": "2026-03-28T13:50:11.9678533",
                                               "status": 404,
                                               "error": "Entity not found",
                                               "message": "Statement not found with id: 3fa85f64-5717-4562-b3fc-2c963f66afa6",
                                               "errors": null
                                             }
                                            """
                            ))),
            @ApiResponse(responseCode = "500", description = "Unexpected error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
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
                            )))
    })
    public ResponseEntity<Void> applyOffer(@Valid @RequestBody LoanOfferDto offer) {
        service.applyOffer(offer);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/calculate/{statementId}")
    @Operation(
            summary = "Calculate credit parameters",
            description = """
                    Updates the statement with final credit calculation: 
                    enriches scoring data, calls Calculator microservice, saves credit entity,
                    and updates statement status.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created entities and generated offers"),
            @ApiResponse(responseCode = "400", description = "Validation error or malformed request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
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
                                                      "message": "Invalid value 'abc' for field 'amount'. Expected type: BigDecimal",
                                                      "errors": null
                                                    }
                                                    """
                                    )
                            }
                    )),
            @ApiResponse(responseCode = "404", description = "Not found entity",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Entity not found",
                                    value = """
                                            {
                                               "timestamp": "2026-03-28T13:50:11.9678533",
                                               "status": 404,
                                               "error": "Entity not found",
                                               "message": "Statement not found with id: 3fa85f64-5717-4562-b3fc-2c963f66afa6",
                                               "errors": null
                                             }
                                            """
                            ))),
            @ApiResponse(responseCode = "422", description = "Denied to issue a loan",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Denied",
                                    value = """
                                            {
                                                "timestamp": "2026-03-28T13:33:47.1076646",
                                                "status": 422,
                                                "error": "Denied",
                                                "message": "Rejection due to: work status - Unemployed",
                                                "errors": null
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "500", description = "Unexpected error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
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
                            )))
    })
    public ResponseEntity<Void> calculateCredit(@Valid @RequestBody FinishRegistrationRequestDto request, @PathVariable String statementId) {
        service.calculateCredit(request, statementId);
        return ResponseEntity.ok().build();
    }
}
