package com.creditbank.statement.client;


import com.creditbank.statement.dto.request.LoanOfferDto;
import com.creditbank.statement.dto.request.LoanStatementRequestDto;
import com.creditbank.statement.exception.StatementNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(DealClient.class)
@Import(DealClientTest.TestConfig.class)
class DealClientTest {

    @Autowired
    private DealClient dealClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public RestClient restClient(RestClient.Builder builder) {
            return builder.build();
        }
    }

    @Test
    void shouldCreateStatementAndReturnOffersFromDeal() throws Exception {
        LoanStatementRequestDto request = LoanStatementRequestDto.builder().term(24).build();
        List<LoanOfferDto> expectedOffers = List.of(LoanOfferDto.builder().term(24).build());

        mockServer.expect(requestTo("/deal/statement"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(objectMapper.writeValueAsString(request)))
                .andRespond(withSuccess(
                        objectMapper.writeValueAsString(expectedOffers),
                        MediaType.APPLICATION_JSON
                ));

        List<LoanOfferDto> result = dealClient.createStatement(request);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTerm()).isEqualTo(24);
        mockServer.verify();
    }

    @Test
    void shouldThrowStatementNotFoundWhenDealReturns404() throws Exception {
        UUID statementId = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");
        LoanOfferDto offer = LoanOfferDto.builder()
                .statementId(statementId)
                .build();

        mockServer.expect(requestTo("/deal/offer/select"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(objectMapper.writeValueAsString(offer)))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .body("Statement not found")
                        .contentType(MediaType.TEXT_PLAIN));

        assertThatThrownBy(() -> dealClient.applyOffer(offer))
                .isInstanceOf(StatementNotFoundException.class)
                .hasMessageContaining("Statement not found - id: " + statementId);

        mockServer.verify();
    }

    @Test
    void shouldHandleOtherHttpErrors() throws Exception {
        LoanOfferDto offer = LoanOfferDto.builder()
                .statementId(UUID.randomUUID())
                .build();

        mockServer.expect(requestTo("/deal/offer/select"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(objectMapper.writeValueAsString(offer)))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThatThrownBy(() -> dealClient.applyOffer(offer))
                .isInstanceOf(HttpServerErrorException.class)
                .hasMessageContaining("500");

        mockServer.verify();
    }
}