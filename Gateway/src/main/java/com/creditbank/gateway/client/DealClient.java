package com.creditbank.gateway.client;

import com.creditbank.gateway.dto.request.FinishRegistrationRequestDto;
import com.creditbank.gateway.dto.request.VerifySesCodeRequest;
import com.creditbank.gateway.exception.BadRequestException;
import com.creditbank.gateway.exception.DeniedException;
import com.creditbank.gateway.exception.StatementNotFoundException;
import com.creditbank.gateway.exception.VerifyException;
import com.creditbank.gateway.utils.ErrorParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
public class DealClient {

    private final RestClient dealClient;
    private final ErrorParser errorParser;

    public DealClient(@Qualifier("dealRestClient") RestClient dealClient, ErrorParser errorParser) {
        this.dealClient = dealClient;
        this.errorParser = errorParser;
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
        } catch (HttpClientErrorException.UnprocessableEntity e) {
            throw new DeniedException(e.getMessage());
        } catch (HttpClientErrorException.BadRequest e) {
            String responseBody = e.getResponseBodyAsString();
            ErrorParser.ParsedError parsedError = errorParser.parse(responseBody);

            throw new BadRequestException(parsedError.getMessage(), parsedError.getErrors());
        }  catch (HttpServerErrorException | ResourceAccessException e) {
            throw new RuntimeException("Deal service is unavailable");
        }
    }

    public void createDocuments(String statementId) {
        try {
            dealClient.post()
                    .uri("/deal/document/" + statementId + "/send")
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound e) {
            throw new StatementNotFoundException("Statement not found - id: " + statementId);
        } catch (HttpServerErrorException | ResourceAccessException e) {
            throw new RuntimeException("Deal service is unavailable");
        }
    }

    public void signDocuments(String statementId) {
        try {
            dealClient.post()
                    .uri("/deal/document/" + statementId + "/sign")
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound e) {
            throw new StatementNotFoundException("Statement not found - id: " + statementId);
        } catch (HttpServerErrorException | ResourceAccessException e) {
            throw new RuntimeException("Deal service is unavailable");
        }
    }

    public void verifySesCode(String statementId, VerifySesCodeRequest code) {
        try {
            dealClient.post()
                    .uri("/deal/document/" + statementId + "/code")
                    .body(code)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound e) {
            throw new StatementNotFoundException("Statement not found - id: " + statementId);
        } catch (HttpClientErrorException.UnprocessableEntity e) {
            throw new VerifyException("The codes do not match");
        } catch (HttpClientErrorException.BadRequest e) {
            String responseBody = e.getResponseBodyAsString();
            ErrorParser.ParsedError parsedError = errorParser.parse(responseBody);

            throw new BadRequestException(parsedError.getMessage(), parsedError.getErrors());
        } catch (HttpServerErrorException | ResourceAccessException e) {
            throw new RuntimeException("Deal service is unavailable");
        }
    }
}
