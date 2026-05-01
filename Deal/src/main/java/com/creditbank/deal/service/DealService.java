package com.creditbank.deal.service;

import com.creditbank.deal.dto.request.FinishRegistrationRequestDto;
import com.creditbank.deal.dto.response.LoanOfferDto;
import com.creditbank.deal.dto.request.LoanStatementRequestDto;
import com.creditbank.deal.entity.Statement;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DealService {

    private final CreditService creditService;
    private final OfferService offerService;
    private final StatementService statementService;
    private final DocumentService documentService;

    @Transactional
    public List<LoanOfferDto> createStatement(LoanStatementRequestDto request) {
        Statement statement = statementService.createStatement(request);
        return offerService.generateOffers(request, statement);
    }

    @Transactional
    public void applyOffer(LoanOfferDto offer) {
        offerService.applyOffer(offer);
        documentService.sendFinishRegistration(offer.getStatementId());
    }

    @Transactional
    public void calculateCredit(FinishRegistrationRequestDto request, String statementId) {
        creditService.calculateCredit(request, statementId);
    }
}
