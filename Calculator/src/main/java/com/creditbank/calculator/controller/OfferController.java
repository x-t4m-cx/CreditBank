package com.creditbank.calculator.controller;

import com.creditbank.calculator.dto.request.LoanStatementRequestDto;
import com.creditbank.calculator.dto.response.LoanOfferDto;
import com.creditbank.calculator.service.OfferService;
import com.creditbank.calculator.api.OfferApi;
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
public class OfferController implements OfferApi {
    private final OfferService offerService;

    @PostMapping("/offers")
    public ResponseEntity<List<LoanOfferDto>> generateOffers(@Valid @RequestBody LoanStatementRequestDto loanStatementRequestDto) {
        return ResponseEntity.ok(
                offerService.generateOffers(loanStatementRequestDto)
        );
    }
}
