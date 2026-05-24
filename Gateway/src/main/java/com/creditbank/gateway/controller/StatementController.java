package com.creditbank.gateway.controller;


import com.creditbank.gateway.api.StatementApi;
import com.creditbank.gateway.dto.request.FinishRegistrationRequestDto;
import com.creditbank.gateway.dto.request.LoanOfferDto;
import com.creditbank.gateway.dto.request.LoanStatementRequestDto;
import com.creditbank.gateway.service.StatementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/statement")
public class StatementController implements StatementApi {

    private final StatementService statementService;
    
    @PostMapping
    public ResponseEntity<List<LoanOfferDto>> createLoanStatement(
            @RequestBody LoanStatementRequestDto loanStatementRequestDto){
        return ResponseEntity.ok(statementService.createLoanStatement(loanStatementRequestDto));
    }

    @PostMapping("/select")
    public ResponseEntity<Void> selectOffer(@RequestBody LoanOfferDto loanOfferDto) {
        statementService.selectOffer(loanOfferDto);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/registration/{statementId}")
    public ResponseEntity<Void> finishRegistration(@RequestBody FinishRegistrationRequestDto request,
                                                   @PathVariable String statementId) {
       statementService.finishRegistration(request, statementId);
       return ResponseEntity.ok().build();
    }
}
