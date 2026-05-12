package com.creditbank.gateway.client;

import com.creditbank.gateway.dto.request.LoanOfferDto;
import com.creditbank.gateway.dto.request.LoanStatementRequestDto;
import com.creditbank.gateway.exception.BadRequestException;
import com.creditbank.gateway.exception.StatementNotFoundException;
import com.creditbank.gateway.utils.ErrorParser;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class StatementClient {

    private final RestClient statementClient;
    private final ErrorParser errorParser;

    public StatementClient(@Qualifier("statementRestClient") RestClient statementClient,
                           ErrorParser errorParser) {
        this.statementClient = statementClient;
        this.errorParser = errorParser;
    }

    public List<LoanOfferDto> createLoanStatement(LoanStatementRequestDto dto) {
        try {
            return statementClient.post()
                    .uri("/statement")
                    .body(dto)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (HttpClientErrorException.BadRequest e) {
            String responseBody = e.getResponseBodyAsString();
            ErrorParser.ParsedError parsedError = errorParser.parse(responseBody);

            throw new BadRequestException(parsedError.getMessage(), parsedError.getErrors());
        } catch (HttpServerErrorException | ResourceAccessException e) {

            throw new RuntimeException("Statement service is unavailable");
        }
    }

    public void selectOffer(LoanOfferDto dto) {
        try {
            statementClient.post()
                    .uri("/statement/offer")
                    .body(dto)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound e) {
            throw new StatementNotFoundException("Statement not found - id: " + dto.getStatementId());
        } catch (HttpClientErrorException.BadRequest e) {
            String responseBody = e.getResponseBodyAsString();
            ErrorParser.ParsedError parsedError = errorParser.parse(responseBody);

            throw new BadRequestException(parsedError.getMessage(), parsedError.getErrors());
        } catch (HttpServerErrorException | ResourceAccessException e) {
            throw new RuntimeException("Statement service is unavailable");
        }
    }
}
