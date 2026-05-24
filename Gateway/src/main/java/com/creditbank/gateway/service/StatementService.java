package com.creditbank.gateway.service;

import com.creditbank.gateway.client.DealClient;
import com.creditbank.gateway.client.StatementClient;
import com.creditbank.gateway.dto.request.FinishRegistrationRequestDto;
import com.creditbank.gateway.dto.request.LoanOfferDto;
import com.creditbank.gateway.dto.request.LoanStatementRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatementService {

    private final DealClient dealClient;
    private final StatementClient statementClient;

    public List<LoanOfferDto> createLoanStatement(LoanStatementRequestDto loanStatementRequestDto) {
        return statementClient.createLoanStatement(loanStatementRequestDto);
    }

    public void selectOffer(LoanOfferDto loanOfferDto) {
        statementClient.selectOffer(loanOfferDto);
    }

    public void finishRegistration(FinishRegistrationRequestDto request, String statementId) {
        dealClient.finishRegistration(request, statementId);
    }
}
