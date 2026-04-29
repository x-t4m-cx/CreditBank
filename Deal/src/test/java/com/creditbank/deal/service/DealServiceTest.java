package com.creditbank.deal.service;

import com.creditbank.deal.dto.request.FinishRegistrationRequestDto;
import com.creditbank.deal.dto.response.LoanOfferDto;
import com.creditbank.deal.dto.request.LoanStatementRequestDto;
import com.creditbank.deal.entity.Statement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DealServiceTest {
    @Mock
    private CreditService creditService;
    @Mock
    private OfferService offerService;
    @Mock
    private DocumentService documentService;
    @Mock
    private StatementService statementService;

    @InjectMocks
    private DealService service;

    @Test
    void shouldCreateStatementAndGenerateOffers() {
        LoanStatementRequestDto requestDto = new LoanStatementRequestDto();
        Statement statement = new Statement();
        LoanOfferDto offer1 = new LoanOfferDto();
        LoanOfferDto offer2 = new LoanOfferDto();
        List<LoanOfferDto> offers = List.of(offer1, offer2);

        when(statementService.createStatement(requestDto)).thenReturn(statement);
        when(offerService.generateOffers(requestDto, statement)).thenReturn(offers);

        List<LoanOfferDto> res = service.createStatement(requestDto);

        assertNotNull(res);
        assertEquals(2, res.size());
        verify(statementService).createStatement(requestDto);
        verify(offerService).generateOffers(requestDto, statement);
    }

    @Test
    void shouldApplyOffer() {
        LoanOfferDto offerDto = new LoanOfferDto();
        doNothing().when(offerService).applyOffer(offerDto);
        service.applyOffer(offerDto);
        verify(offerService).applyOffer(offerDto);
    }

    @Test
    void shouldCalculateCredit() {
        FinishRegistrationRequestDto request = new FinishRegistrationRequestDto();
        String statementId = UUID.randomUUID().toString();
        doNothing().when(creditService).calculateCredit(request, statementId);
        service.calculateCredit(request, statementId);
        verify(creditService).calculateCredit(request, statementId);
    }
}