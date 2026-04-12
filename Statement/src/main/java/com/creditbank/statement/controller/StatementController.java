package com.creditbank.statement.controller;

import com.creditbank.statement.dto.request.LoanOfferDto;
import com.creditbank.statement.dto.request.LoanStatementRequestDto;
import com.creditbank.statement.service.StatementService;
import com.creditbank.statement.api.StatementApi;
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
@RequestMapping("/statement")
public class StatementController implements StatementApi {

    private final StatementService statementService;

    @PostMapping
    public ResponseEntity<List<LoanOfferDto>> createStatement(@RequestBody @Valid LoanStatementRequestDto request) {
        return ResponseEntity.ok(statementService.createStatement(request));
    }

    @PostMapping("/offer")
    public ResponseEntity<Void> applyOffer(@RequestBody @Valid LoanOfferDto offerDto) {
        statementService.applyOffer(offerDto);
        return ResponseEntity.ok().build();
    }
}
