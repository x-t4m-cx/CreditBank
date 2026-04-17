package com.creditbank.statement.service;

import com.creditbank.statement.client.DealClient;
import com.creditbank.statement.dto.request.LoanOfferDto;
import com.creditbank.statement.dto.request.LoanStatementRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatementServiceTest {

    @Mock
    private DealClient dealClient;

    @InjectMocks
    private StatementService statementService;

    @Test
    void shouldCreateStatementAndReturnOffers() {
        LoanStatementRequestDto request = LoanStatementRequestDto.builder()
                .term(24)
                .build();

        List<LoanOfferDto> offers = List.of(LoanOfferDto.builder().term(24).build());
        when(dealClient.createStatement(request)).thenReturn(offers);

        assertThat(statementService.createStatement(request)).isSameAs(offers);
        verify(dealClient).createStatement(request);
    }

    @Test
    void shouldApplyOfferByDelegatingToDealClient() {
        LoanOfferDto offer = LoanOfferDto.builder().term(24).build();

        statementService.applyOffer(offer);

        verify(dealClient).applyOffer(offer);
    }
}


