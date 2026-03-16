package com.creditbank.calculator.controller;

import com.creditbank.calculator.dto.request.ScoringDataDto;
import com.creditbank.calculator.dto.response.CreditDto;
import com.creditbank.calculator.service.CreditService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreditControllerTest {

    @Mock
    private CreditService creditService;

    @InjectMocks
    private CreditController creditController;

    @Test
    void shouldReturnCreditAndStatusOkWhenRequestValid() {
        ScoringDataDto request = ScoringDataDto.builder().build();
        CreditDto credit = CreditDto.builder()
                .amount(new BigDecimal("100000"))
                .term(12)
                .monthlyPayment(new BigDecimal("8884.88"))
                .rate(new BigDecimal("12"))
                .psk(new BigDecimal("106618.56"))
                .build();
        when(creditService.calculateCredit(request)).thenReturn(credit);

        ResponseEntity<CreditDto> response = creditController.calculateCredit(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(new BigDecimal("100000"), response.getBody().getAmount());
        assertEquals(12, response.getBody().getTerm());
        verify(creditService).calculateCredit(request);
    }

    @Test
    void shouldDelegateToServiceWithSameRequest() {
        ScoringDataDto request = ScoringDataDto.builder()
                .amount(new BigDecimal("250000"))
                .term(24)
                .build();
        CreditDto credit = CreditDto.builder().amount(request.getAmount()).build();
        when(creditService.calculateCredit(request)).thenReturn(credit);

        creditController.calculateCredit(request);

        verify(creditService).calculateCredit(request);
    }
}
