package com.creditbank.calculator.controller;

import com.creditbank.calculator.dto.request.ScoringDataDto;
import com.creditbank.calculator.dto.response.CreditDto;
import com.creditbank.calculator.service.CreditService;
import com.creditbank.calculator.api.CreditApi;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/calculator")
public class CreditController implements CreditApi {
    private final CreditService CreditService;

    @PostMapping("/calc")
    public ResponseEntity<CreditDto> calculateCredit(@RequestBody @Valid ScoringDataDto scoringDataDto) {
        return ResponseEntity.ok(
                CreditService.calculateCredit(scoringDataDto)
        );
    }
}
