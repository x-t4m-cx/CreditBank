package com.creditbank.deal.controller;

import com.creditbank.deal.dto.request.FinishRegistrationRequestDto;
import com.creditbank.deal.dto.response.LoanOfferDto;
import com.creditbank.deal.dto.request.LoanStatementRequestDto;
import com.creditbank.deal.service.DealService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DealControllerTest {

    @Mock
    private DealService service;

    @InjectMocks
    private DealController controller;

    @Test
    void shouldCreateStatementAndReturnOffers() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        List<LoanOfferDto> offers = List.of(new LoanOfferDto(), new LoanOfferDto());

        when(service.createStatement(request)).thenReturn(offers);

        ResponseEntity<List<LoanOfferDto>> response = controller.createStatement(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(offers, response.getBody());
        verify(service).createStatement(request);
    }

    @Test
    void shouldApplyOfferAndReturnOk() {
        LoanOfferDto offer = new LoanOfferDto();
        doNothing().when(service).applyOffer(offer);

        ResponseEntity<Void> response = controller.applyOffer(offer);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(service).applyOffer(offer);
    }

    @Test
    void shouldCalculateCreditAndReturnOk() {
        FinishRegistrationRequestDto request = new FinishRegistrationRequestDto();
        String statementId = "123e4567-e89b-12d3-a456-426614174000";
        doNothing().when(service).calculateCredit(request, statementId);

        ResponseEntity<Void> response = controller.calculateCredit(request, statementId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(service).calculateCredit(request, statementId);
    }
}

