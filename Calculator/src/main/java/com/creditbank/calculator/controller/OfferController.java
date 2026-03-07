package com.creditbank.calculator.controller;

import com.creditbank.calculator.dto.request.LoanStatementRequestDto;
import com.creditbank.calculator.dto.response.LoanOfferDto;
import com.creditbank.calculator.exception.ErrorResponse;
import com.creditbank.calculator.service.OfferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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
            @ApiResponse(responseCode = "200", description = "Offers generated"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<List<LoanOfferDto>> generateOffers(
            @Valid
            @RequestBody
            LoanStatementRequestDto loanStatementRequestDto
    ){
        List<LoanOfferDto> offers = offerService
                .generateOffers(loanStatementRequestDto);
        return ResponseEntity.ok(offers);
    }
}
