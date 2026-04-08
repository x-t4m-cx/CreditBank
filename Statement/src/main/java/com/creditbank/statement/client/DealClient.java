package com.creditbank.statement.client;

import com.creditbank.statement.dto.request.LoanOfferDto;
import com.creditbank.statement.dto.request.LoanStatementRequestDto;
import com.creditbank.statement.exception.StatementNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DealClient {

    private final RestClient restClient;

    public List<LoanOfferDto> createStatement(LoanStatementRequestDto request) {
        return restClient.post()
                .uri("/deal/statement")
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public void applyOffer(LoanOfferDto offerDto) {
        try {
            restClient.post()
                    .uri("/deal/offer/select")
                    .body(offerDto)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound e) {
            throw new StatementNotFoundException("Statement not found - id: " + offerDto.getStatementId());
        }

    }
}
