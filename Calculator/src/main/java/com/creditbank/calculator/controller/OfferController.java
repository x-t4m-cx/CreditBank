package com.creditbank.calculator.controller;

import com.creditbank.calculator.dto.request.LoanStatementRequestDto;
import com.creditbank.calculator.dto.response.LoanOfferDto;
import com.creditbank.calculator.dto.response.ErrorResponse;
import com.creditbank.calculator.service.OfferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/calculator")
@Tag(name = "Loan Offers", description = "Controller for generating loan offers and prescoring data processing ")
public class OfferController {
    private final OfferService offerService;

    @PostMapping("/offers")
    @Operation(
            summary = "Generate loan offers",
            description = "Returns available loan offers based on statement data."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Offers generated"
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
                                          "message": "Invalid value 'abc' for field 'amount'. Expected type: BigDecimal",
                                          "errors": null
                                        }
                                        """
                                    )
                            }
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
    public ResponseEntity<List<LoanOfferDto>> generateOffers(@Valid @RequestBody LoanStatementRequestDto loanStatementRequestDto){
        List<LoanOfferDto> offers = offerService
                .generateOffers(loanStatementRequestDto);
        return ResponseEntity.ok(offers);
    }
}
