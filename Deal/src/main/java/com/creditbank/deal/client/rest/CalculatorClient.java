package com.creditbank.deal.client.rest;

import com.creditbank.deal.dto.calculator.response.CreditDto;
import com.creditbank.deal.dto.response.LoanOfferDto;
import com.creditbank.deal.dto.request.LoanStatementRequestDto;
import com.creditbank.deal.dto.calculator.request.ScoringDataDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CalculatorClient {
    private final RestClient restClient;

    public List<LoanOfferDto> generateOffers(LoanStatementRequestDto request) {
        return restClient.post()
                .uri("/calculator/offers")
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public CreditDto calculateCredit(ScoringDataDto scoringDataDto) {
        return restClient.post()
                .uri("/calculator/calc")
                .body(scoringDataDto)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }
}
