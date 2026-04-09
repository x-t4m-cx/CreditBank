package com.creditbank.deal.service;

import com.creditbank.deal.client.CalculatorClient;
import com.creditbank.deal.dto.response.LoanOfferDto;
import com.creditbank.deal.dto.request.LoanStatementRequestDto;
import com.creditbank.deal.entity.Statement;
import com.creditbank.deal.enums.ApplicationStatus;
import com.creditbank.deal.enums.ChangeType;
import com.creditbank.deal.mapper.LoanOfferMapper;
import com.creditbank.deal.model.LoanOffer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OfferServiceTest {

    @Mock
    private CalculatorClient calculatorClient;
    @Mock
    private StatementService statementService;
    @Mock
    private LoanOfferMapper mapper;
    @InjectMocks
    private OfferService service;

    @Test
    void shouldGenerateOffersWhenValidRequestAndStatementProvided() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        Statement statement = new Statement();
        statement.setStatementId(UUID.randomUUID());
        List<LoanOfferDto> mockOffers = new ArrayList<>();
        LoanOfferDto offer = new LoanOfferDto();
        offer.setStatementId(statement.getStatementId());
        mockOffers.add(offer);

        when(calculatorClient.generateOffers(any(LoanStatementRequestDto.class)))
                .thenReturn(mockOffers);

        List<LoanOfferDto> result = service.generateOffers(request, statement);

        assertNotNull(result);
        assertEquals(mockOffers.size(), result.size());
        assertEquals(statement.getStatementId(), result.getFirst().getStatementId());
        verify(calculatorClient).generateOffers(request);
    }

    @Test
    void shouldApplyOfferWhenValidOfferProvided() {
        LoanOfferDto offer = new LoanOfferDto();
        offer.setStatementId(UUID.randomUUID());
        Statement statement = new Statement();
        statement.setStatementId(offer.getStatementId());

        when(statementService.getStatementByIdWithLock(any(UUID.class))).thenReturn(statement);
        when(mapper.toModel(any(LoanOfferDto.class))).thenReturn(new LoanOffer());

        service.applyOffer(offer);

        assertNotNull(statement.getAppliedOffer());
        verify(statementService).setStatus(statement, ApplicationStatus.APPROVED, ChangeType.MANUAL);
        verify(statementService).updateStatement(statement);
    }
}