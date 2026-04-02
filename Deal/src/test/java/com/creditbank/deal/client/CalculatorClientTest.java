package com.creditbank.deal.client;

import com.creditbank.deal.dto.calculator.response.CreditDto;
import com.creditbank.deal.dto.response.LoanOfferDto;
import com.creditbank.deal.dto.request.LoanStatementRequestDto;
import com.creditbank.deal.dto.calculator.request.ScoringDataDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculatorClientTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private RestClient restClient;

    @InjectMocks
    private CalculatorClient client;

    @SuppressWarnings("unchecked")
    @Test
    void shouldGenerateOffersWhenValidRequestProvided() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        List<LoanOfferDto> expected = List.of(new LoanOfferDto(), new LoanOfferDto());

        when(restClient.post()
                .uri("/calculator/offers")
                .body(request)
                .retrieve()
                .body(any(ParameterizedTypeReference.class)))
                .thenReturn(expected);

        List<LoanOfferDto> actual = client.generateOffers(request);

        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertEquals(expected, actual);
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldCalculateCreditWhenValidScoringDataProvided() {
        ScoringDataDto scoringData = new ScoringDataDto();
        CreditDto expected = new CreditDto();

        when(restClient.post()
                .uri("/calculator/calc")
                .body(scoringData)
                .retrieve()
                .body(any(ParameterizedTypeReference.class)))
                .thenReturn(expected);

        CreditDto actual = client.calculateCredit(scoringData);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }
}

