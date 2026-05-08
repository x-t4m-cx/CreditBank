package com.creditbank.gateway.client;

import com.creditbank.gateway.dto.request.FinishRegistrationRequestDto;
import com.creditbank.gateway.exception.StatementNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
public class DealClient {

    private final RestClient dealClient;

    public DealClient(@Qualifier("dealRestClient") RestClient dealClient) {
        this.dealClient = dealClient;
    }

    public void finishRegistration(FinishRegistrationRequestDto dto, String statementId) {
        try {
            dealClient.post()
                    .uri("/deal/calculate/" + statementId)
                    .body(dto)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound e) {
            throw new StatementNotFoundException("Statement not found - id: " + statementId);
        }
    }
}
