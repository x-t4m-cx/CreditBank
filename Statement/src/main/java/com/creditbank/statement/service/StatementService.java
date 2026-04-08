package com.creditbank.statement.service;

import com.creditbank.statement.dto.request.LoanOfferDto;
import com.creditbank.statement.dto.request.LoanStatementRequestDto;
import lombok.RequiredArgsConstructor;
import com.creditbank.statement.client.DealClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatementService {

    private final DealClient dealClient;
    public List<LoanOfferDto> createStatement(LoanStatementRequestDto request) {
        return dealClient.createStatement(request);
    }

    public void applyOffer(LoanOfferDto offerDto) {
        dealClient.applyOffer(offerDto);
    }
}
