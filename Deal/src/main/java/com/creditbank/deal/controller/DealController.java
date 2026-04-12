package com.creditbank.deal.controller;

import com.creditbank.deal.dto.request.FinishRegistrationRequestDto;
import com.creditbank.deal.dto.response.LoanOfferDto;
import com.creditbank.deal.dto.request.LoanStatementRequestDto;
import com.creditbank.deal.service.DealService;
import com.creditbank.deal.api.DealApi;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/deal")
public class DealController implements DealApi {
    private final DealService service;

    @PostMapping("/statement")
    public ResponseEntity<List<LoanOfferDto>> createStatement(
            @Valid @RequestBody LoanStatementRequestDto request
    ) {
        return ResponseEntity.ok(service.createStatement(request));
    }


    @PostMapping("/offer/select")
    public ResponseEntity<Void> applyOffer(
            @Valid @RequestBody LoanOfferDto offer
    ) {
        service.applyOffer(offer);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/calculate/{statementId}")

    public ResponseEntity<Void> calculateCredit(
            @Valid @RequestBody FinishRegistrationRequestDto request,
            @PathVariable String statementId
    ) {
        service.calculateCredit(request, statementId);
        return ResponseEntity.ok().build();
    }
}
