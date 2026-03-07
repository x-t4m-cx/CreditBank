package com.creditbank.calculator.controller;

import com.creditbank.calculator.dto.request.ScoringDataDto;
import com.creditbank.calculator.dto.response.CreditDto;
import com.creditbank.calculator.exception.ErrorResponse;
import com.creditbank.calculator.service.CreditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/calculator")
@Tag(name = "Credit", description = "Controller for credit calculation and scoring data processing")
public class CreditController {
    private final CreditService CreditService;

    @PostMapping("/calc")
    @Operation(
            summary = "Calculate credit conditions",
            description = "Performs calculateCredit and returns credit conditions with payment schedule."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Credit calculated"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation or calculateCredit error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<CreditDto> calculateCredit(@RequestBody ScoringDataDto scoringDataDto) {
        return ResponseEntity.ok(
                CreditService.calculateCredit(scoringDataDto)
        );
    }
}
