package com.creditbank.gateway.client;

import com.creditbank.gateway.dto.request.LoanOfferDto;
import com.creditbank.gateway.dto.request.LoanStatementRequestDto;
import com.creditbank.gateway.exception.StatementNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class StatementClient {

    private final RestClient statementClient;

    public StatementClient(@Qualifier("statementRestClient") RestClient statementClient) {
        this.statementClient = statementClient;
    }

    public List<LoanOfferDto> createLoanStatement(LoanStatementRequestDto dto) {
        return statementClient.post()
                .uri("/statement")
                .body(dto)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public void selectOffer(LoanOfferDto dto) {
        try {
            statementClient.post()
                    .uri("/deal/offer/select")
                    .body(dto)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound e) {
            throw new StatementNotFoundException("Statement not found - id: " + dto.getStatementId());
        }
    }
}
