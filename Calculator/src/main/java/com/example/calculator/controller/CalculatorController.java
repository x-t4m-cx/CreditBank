package com.example.calculator.controller;

import com.example.calculator.dto.CreditDto;
import com.example.calculator.dto.LoanOfferDto;
import com.example.calculator.dto.LoanStatementRequestDto;
import com.example.calculator.dto.ScoringDataDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/calculator")
public class CalculatorController {

    @PostMapping("/offers")
    public ResponseEntity<List<LoanOfferDto>> offers(LoanStatementRequestDto loanStatementRequestDto){
        //TODO
    }

    @PostMapping("/calc")
    public ResponseEntity<CreditDto> calc(ScoringDataDto scoringDataDto) {
        //TODO
    }
}
